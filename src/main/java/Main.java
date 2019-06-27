import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.log4j.Logger;





public class Main {

    private static Logger logger = Logger.getLogger(Main.class);


    public static String REGEX_UUID = "[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}";

    public static String REGEX_DATE = "([0-9]{8}).*?";

    public static String REGEX_UUID_DATE = REGEX_UUID + "\\_" +REGEX_DATE;

    public String concatAndConvertString(String str1, String str2){
        String concatedString=str1.concat(str2);
        return concatedString.toUpperCase();
    }


    public static void main(String[] args) {

        logger.info("program start");

        /* verify program arguments
         *
         * args[0] : data input directory path: that contains data to process
         * args[1] : temp directory path: to store temp files
         * args[2] : output directory path: to store results
         * args[3] : processDate
         *
         */

        if(args.length != 4)
        {
            logger.error("missed argument: program need 4 arguments to start: " +
                    "inputPath " +
                    "tempPath " +
                    "outputPath " +
                    "processDate");
            System.exit(0);
        }



        //get main Arguments
        String inputPath = args[0];
        if (!Files.exists(Paths.get(inputPath))) {
            logger.info("There is no data to process. Please verify the input directory path!");
            System.exit(0);
        }

        String tempPath = args[1];

        if (!Files.exists(Paths.get(tempPath))) {
            new File(tempPath).mkdirs();
        }

        String outputPath = args[2];
        if (!Files.exists(Paths.get(outputPath))) {
            new File(outputPath).mkdirs();
        }


        String processDate = args[3];


        // get dates of the six days before processDate
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String[] dates = new String[7];//{"20190623","20190624","20190625","20190626","20190627","20190628","20190629"};
        long DAY_IN_MS = 1000 * 60 * 60 * 24;
        for(int i = 0; i < 7; i++){

            Date date = null;
            try {
                date = new Date(dateFormat.parse(processDate).getTime() - ( (6 - i ) * DAY_IN_MS));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dates[i] = dateFormat.format(date);
            System.out.println(dates[i]);
        }



        /*
        * input data directory: process data in inputPath/transactions_YYYYMMDD.data where date = processDate
        *
        * partitionning: partitionning transactions_YYYYMMDD.data into transactions_UUID_YYYYMMDD.data
        *
        * temp data : transactions_UUID__YYYYMMDD.data will be stored in tempPath/transactions/transactions_UUID__YYYYMMDD.data
        *
        * output results: foreach transactions file per store calculate top 100 sales
        *
        * */


        logger.info("get distincts store ids");
        List<String> storeIds = Calculator.getAllStoresIds(inputPath,REGEX_UUID_DATE, REGEX_UUID, REGEX_DATE, processDate);

        logger.info("partitionning of " + inputPath + "/transactions_" + processDate + ".data");
        Partitionner.transactionsFilePartitionner(inputPath + "/transactions_" + processDate + ".data", tempPath+ "/transactions", REGEX_DATE, storeIds);

        storeIds.forEach(storeId ->{

            logger.info("calculate top 100 sales for store: " + storeId +  " at: " + processDate);
            Calculator.getBestSaleStore(tempPath + "/transactions/transactions_" + storeId + "_" + processDate + ".data", REGEX_UUID_DATE, outputPath);
        });






        /*
        *
        * input data: tempPath/transactions: transactions per store for each day, inputPah: products references
        *
        * transformation: join (transactions x products references) in productId to calculate transaction price
        *
        * merge : merge transactions file per store per day into transaction file per store
        *
        * output : foreach store calculate top 100 CA for the last 7 days by reducing the resulted merged file
        *
         */


        // skip processDate transactions file because it's alerady partitionned
        // partitionning the last seven day transactions file excluded of processDate transactions file

        for (String day: dates){

            if(!day.equals(processDate)){
                logger.info("partitionning of " + inputPath + "/transactions_" + day + ".data");
                Partitionner.transactionsFilePartitionner(inputPath + "/transactions_" + day + ".data", tempPath+ "/transactions", REGEX_DATE, storeIds);
            }
        }


       storeIds.forEach( storeId -> {
            List<File> listFiles = new ArrayList<>();
            for (String day: dates){

                logger.info("mapping file: " + tempPath + "/transactions/transactions_" + storeId + "_" + day + ".data");
                Calculator.mapJoin(tempPath + "/transactions/transactions_" + storeId + "_" + day + ".data",inputPath + "/reference_prod-" + storeId + "_" + day + ".data", REGEX_UUID_DATE, tempPath);
                listFiles.add(new File(tempPath + "/mappedTransactions/transactions_" + storeId + "_" + day + ".data"));
            }

            try {
                logger.info("merge transactions files of store: " + storeId);
                Compactionner.mergeFiles(listFiles, tempPath + "/merge/transactions_" + storeId + ".data.merge", tempPath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            logger.info("calculate top 100 CA for the store: " + storeId + " for the last seven days");
            Calculator.getBestProductsCaPerStoreSevenDays(tempPath + "/merge/transactions_" + storeId + ".data.merge", REGEX_UUID, outputPath, processDate);
        });


    }

}

