import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class Generator {

    private int nbStores;
    private int nbProductsByStore;
    private long nbTransactionsByDay;
    private String date;
    private String[] storeIds;
    private Random rd;

    final int millisInDay = 24*60*60*1000;




    public Generator(int nbStores, int nbProductsByStore, long nbTransactionsByDay, String date, Boolean lastSevenDays){

        this.nbStores = nbStores;
        this.nbProductsByStore = nbProductsByStore;
        this.nbTransactionsByDay = nbTransactionsByDay;
        this.date = date;
        this.storeIds = new String[this.nbStores];
        this.rd = new Random();


        generateStoreIds();

        if(lastSevenDays){
            // get dates of the six days before processDate
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            String[] dates = new String[7];
            long DAY_IN_MS = 1000 * 60 * 60 * 24;
            for(int i = 0; i < 7; i++){

                Date day = null;
                try {
                    day = new Date(dateFormat.parse(date).getTime() - ( (6 - i ) * DAY_IN_MS));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                dates[i] = dateFormat.format(day);
            }

            for(String day: dates){

                for(String storeId: storeIds)
                    generateStoreProductsReferenceFile(storeId, day);

                generateTransactionsFile(day);
            }

        } else {
            for(String storeId: storeIds)
                generateStoreProductsReferenceFile(storeId, date);

            generateTransactionsFile(date);
        }

    }



    public void generateStoreIds(){

        byte[] arr = new byte[16];
        String storeId = "";

        for(int i=0; i < nbStores; i++){

            // generate random store id
            rd.nextBytes(arr);
            StringBuilder sb = new StringBuilder();
            for (byte b : arr) {
                sb.append(String.format("%02x", b));
            }

            storeId = sb.toString();
            storeId = storeId.substring(0,8) + '-' + storeId.substring(8,12) + '-' + storeId.substring(12,16) + '-' + storeId.substring(16,20)
                    + '-' + storeId.substring(20);

            this.storeIds[i] = storeId;
        }
    }



    public void generateStoreProductsReferenceFile(String storeId, String day){

        // create reference product file: reference-prod-idMagasin_YYYYMMDD.data
        new File("data").mkdirs();

        File file = new File("data/reference_prod-" + storeId + "_" + day + ".data");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String line = "";
        FileWriter fw = null;
        try {
            fw = new FileWriter("data/reference_prod-" + storeId + "_" + day + ".data");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // generate product reference data
        for (int j=0; j < this.nbProductsByStore; j++){
            //write product reference: product | price
            line = Integer.toString(j + 1) + "|" + Float.toString(rd.nextFloat()*100) + "\n";

            try {
                fw.write(line);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        try {
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    public void generateTransactionsFile(String day){

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss.sssZ").format(Calendar.getInstance().getTime());
        String zone = timeStamp.substring(timeStamp.indexOf('+'));

        File file = new File("data/transactions_" + day + ".data");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String line = "";
        FileWriter fw = null;
        try {
            fw = new FileWriter("data/transactions_" + day + ".data");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // generate transactions
        for (long j=0; j < this.nbTransactionsByDay; j++){
            //write transaction: txId|datetime|magasin|produit|qte

            int repeatTransaction = rd.nextInt(9) + 1;
            int indexStore = rd.nextInt(nbStores);

            Time time = new Time((long)rd.nextInt(millisInDay));
            String timeString = time.toString();
            timeString = "T" + timeString.replaceAll(":","") + zone;


            for(int r=0; r<repeatTransaction;r++){
                int productId = rd.nextInt(nbProductsByStore) + 1;
                int qte = rd.nextInt(9) + 1;
                line = Long.toString(j + 1) + "|" + day + timeString + "|" + storeIds[indexStore] + "|" + productId + "|" + qte + "\n";

                try {
                    fw.write(line);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


        }


        try {
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
