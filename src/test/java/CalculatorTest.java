
import org.junit.Test;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;


public class CalculatorTest {


    public String getInputPath(String fileName){

        ClassLoader classLoader = getClass().getClassLoader();

        File file = new File(classLoader.getResource("data").getFile());

        return file.getAbsolutePath();

    }


     @Test
     public void testgetBestSaleStoreNumberOfFiles() throws Exception {

        String dataPath = getInputPath("data");


        List<String> storeIds = Calculator.getAllStoresIds(dataPath, Main.REGEX_UUID_DATE, Main.REGEX_UUID, Main.REGEX_DATE, "20190629");

        Partitionner.transactionsFilePartitionner(dataPath + "/transactions_" + "20190629" + ".data", "temp" + "/transactions", Main.REGEX_DATE, storeIds);

        storeIds.forEach(storeId ->{

            Calculator.getBestSaleStore("temp" + "/transactions/transactions_" + storeId + "_" + "20190629" + ".data", Main.REGEX_UUID_DATE, "output");
        });


        assertEquals(new File("temp" + "/transactions").listFiles().length , 2);

        assertEquals(new File("output").listFiles().length, 2);

         Compactionner.deleteDirectory(new File("temp"));

        Compactionner.deleteDirectory(new File("output"));
    }




    @Test
    public void testgetBestSaleStoreOutputResults() throws Exception {

        String dataPath = getInputPath("data");

        List<String> storeIds = Calculator.getAllStoresIds(dataPath, Main.REGEX_UUID_DATE, Main.REGEX_UUID, Main.REGEX_DATE, "20190629");

        Partitionner.transactionsFilePartitionner(dataPath + "/transactions_" + "20190629" + ".data", "temp" + "/transactions", Main.REGEX_DATE, storeIds);

        storeIds.forEach(storeId ->{

            Calculator.getBestSaleStore("temp" + "/transactions/transactions_" + storeId + "_" + "20190629" + ".data", Main.REGEX_UUID_DATE, "output");
        });


        List<Integer> list =  Files.lines(Paths.get("output/top_100_ventes_d86aa95d-66f3-b4c5-0366-07e89476fbb3_20190629.data"), Charset.defaultCharset())
                .map(line -> {
                    String[] arr = line.split("\\|");
                    return new Integer(arr[1]);
                })
                .collect( Collectors.toList());

        Integer line1 = list.get(0);
        Integer line2 = list.get(1);
        Integer line3 = list.get(2);

        Integer val1 = 19;
        Integer val2 = 17;
        Integer val3 = 7;

        assertEquals(line1, val1);
        assertEquals(line2, val2);
        assertEquals(line3, val3);

        Compactionner.deleteDirectory(new File("temp"));
        Compactionner.deleteDirectory(new File("output"));
    }

}

