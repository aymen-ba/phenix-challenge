import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


public class Compactionner {



    public static String mergeFiles(List<File> listeFile, String fileName, String outputPath) throws IOException {

        if (!Files.exists(Paths.get(outputPath + "/merge"))) {
            new File(outputPath + "/merge").mkdirs();
        }


        if(listeFile.size() != 0){


            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));

            listeFile.forEach(item -> {

                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new FileReader(item));
                } catch (FileNotFoundException e) {
                }

                String line = null;

                while (true) {
                    try {
                        if ((line = reader.readLine()) == null) break;
                        writer.write(line);
                        writer.newLine();

                    } catch (IOException e) {
                    }
                }

                try {
                    reader.close();
                } catch (IOException e) {
                }
            });
            writer.close();
        }
        return fileName;

    }


    public static boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if(allContents != null){
            for (File file : allContents){
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

}
