
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class CalculatorTest {


     @Test
     public void testgetBestSaleStore() throws Exception {

        Compactionner.deleteDirectory(new File("data"));

        Compactionner.deleteDirectory(new File("temp"));

        Compactionner.deleteDirectory(new File("output"));


        Generator g = new Generator(1200, 1000, 1000000, "20190629",false);

        List<String> storeIds = Calculator.getAllStoresIds("data", Main.REGEX_UUID_DATE, Main.REGEX_UUID, Main.REGEX_DATE, "20190629");

        Partitionner.transactionsFilePartitionner("data" + "/transactions_" + "20190629" + ".data", "temp"+ "/transactions", Main.REGEX_DATE, storeIds);

        storeIds.forEach(storeId ->{

            Calculator.getBestSaleStore("temp" + "/transactions/transactions_" + storeId + "_" + "20190629" + ".data", Main.REGEX_UUID_DATE, "output");
        });

        assertEquals(new File("temp/transactions").listFiles().length , 1200);

        assertEquals(new File("output").listFiles().length, 1200);


        Compactionner.deleteDirectory(new File("data"));

        Compactionner.deleteDirectory(new File("temp"));

        Compactionner.deleteDirectory(new File("output"));
    }



    @Test
    public void testBestCALastSevenDay() throws Exception {
        Compactionner.deleteDirectory(new File("data"));

        Compactionner.deleteDirectory(new File("temp"));

        Compactionner.deleteDirectory(new File("output"));


        Generator g1 = new Generator(1200, 1000, 1000000, "20190629", true);



        String[] dates = {"20190623","20190624","20190625","20190626","20190627","20190628","20190629"};


        List<String> storeIds = Calculator.getAllStoresIds("data", Main.REGEX_UUID_DATE, Main.REGEX_UUID, Main.REGEX_DATE, "20190629");

        for (String day: dates){

            Partitionner.transactionsFilePartitionner("data" + "/transactions_" + day + ".data", "temp"+ "/transactions", Main.REGEX_DATE, storeIds);

        }

        storeIds.forEach( storeId -> {
            List<File> listFiles = new ArrayList<>();
            for (String day: dates){

                Calculator.mapJoin("temp" + "/transactions/transactions_" + storeId + "_" + day + ".data","data" + "/reference_prod-" + storeId + "_" + day + ".data", Main.REGEX_UUID_DATE, "temp");
                listFiles.add(new File("temp" + "/mappedTransactions/transactions_" + storeId + "_" + day + ".data"));
            }

            try {
                Compactionner.mergeFiles(listFiles, "temp" + "/merge/transactions_" + storeId + ".data.merge", "temp");
            } catch (IOException e) {
                e.printStackTrace();
            }

            Calculator.getBestProductsCaPerStoreSevenDays("temp" + "/merge/transactions_" + storeId + ".data.merge", Main.REGEX_UUID, "output", "20190629");
        });


        assertEquals(new File("temp/transactions").listFiles().length , 1200 * 7);

        assertEquals(new File("temp/mappedTransactions").listFiles().length, 1200 * 7);

        assertEquals(new File("output").listFiles().length, 1200);

        Compactionner.deleteDirectory(new File("data"));

        Compactionner.deleteDirectory(new File("temp"));

        Compactionner.deleteDirectory(new File("output"));
    }

}

