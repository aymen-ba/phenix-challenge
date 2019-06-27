import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;


public class Partitionner {



    public  static Map<String,FileWriter> streams ;


    // Read and write file using lines() and Stream Approach
    public static void transactionsFilePartitionner(String inputFile, String outputPath, String regexDate, List<String> storeIds) {

        if (!Files.exists(Paths.get(outputPath))) {
            new File(outputPath).mkdirs();
        }

        Stream<String> stream = null;
        try {

            openStreams(outputPath, inputFile, storeIds, regexDate);

            stream = Files.lines(Paths.get(inputFile));

            stream.forEach(line -> {
                try {

                    String id = line.split("\\|")[2];

                    streams.get(id).write(line + "\n");

                    streams.get(id).flush();

                } catch (IOException e) {
                    closeStreams();
                }

            });

            closeStreams();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }





    public  static void openStreams(String outputPath, String inputFile, List<String> storeIds, String regexDate) throws IOException {

        if (streams == null) {
            streams = new HashMap<>();

            storeIds.forEach(storeId ->
                    {
                        try {
                            streams.put(storeId, new FileWriter(outputPath + "/transactions_" + storeId + "_" + Calculator.getSubString(inputFile, regexDate)+ ".data"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }

    }




    public static void closeStreams() {
        for (Map.Entry<String, FileWriter> entry: streams.entrySet()) {
            try {
                entry.getValue().close();
            } catch (IOException e) {
            }
        }
        streams.clear();
        streams = null;
    }


}
