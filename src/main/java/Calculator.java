import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Calculator {


    //Return storeId List from files Name and filter
    public static List<String> getAllStoresIds(String path, String patternRegex, String regexUUID, String regexDate, String date) {

        List<String> storeIdList = new ArrayList<>();

        getFilesName(path).forEach(fileN -> {
            if (getSubString(fileN.toString(), patternRegex) != null && getSubString(fileN.toString(), regexDate).equals(date)) {
                storeIdList.add(getSubString(fileN.toString(), regexUUID));
            }
        });


        return storeIdList;
    }



    //Read folder and get files Name
    public static List<File> getFilesName(String path) {
        List<File> filesInFolder = null;
        try {
            filesInFolder = Files.walk(Paths.get(path))
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        } catch (IOException e) {

        }
        return filesInFolder;
    }



    //Return storeId from files Name
    public static String getSubString(String fileN, String patternRegex) {
        Pattern pattern;
        Matcher matcher;
        String value = null;
        try {
            pattern = Pattern.compile(patternRegex);
            matcher = pattern.matcher(fileN);
            while (matcher.find()) {
                value = matcher.group(0);

            }
        } catch (IllegalStateException e) {

        }
        return value;
    }



    // Compute the 100 best Sales/day/store
    public static void getBestSaleStore(String inputFile, String regex, String outputPath) {

        if (!Files.exists(Paths.get(outputPath))) {
            new File(outputPath).mkdirs();
        }

        Set<Transaction> transactions = new HashSet<>();
        Map<String, String> totalByProduitSorted = new LinkedHashMap<>();

        getStream(inputFile).forEach(it -> {
            String[] s = it.split("\\|");
            transactions.add(new Transaction(s[1], Long.parseLong(s[3]), Long.parseLong(s[4])));
        });

        transactions
                .parallelStream()
                .collect(Collectors.groupingBy(Transaction::getProduit,
                        Collectors.summingLong(Transaction::getQuantite)))
                .entrySet().parallelStream()
                .sorted(Map.Entry.<Long, Long>comparingByValue()
                        .reversed())
                .limit(100)
                .forEachOrdered(e -> totalByProduitSorted.put(e.getKey().toString(), e.getValue().toString()));


        resultFileCreator(totalByProduitSorted, outputPath + "/top_100_ventes_" +  getSubString(inputFile, regex));
    }


    //Create Stream with file path
    public static Stream<String> getStream(String path) {

        Stream<String> stream = Stream.empty();
        try {
            stream = Files.lines(Paths.get(path));
        } catch (IOException ex) {
        }
        return stream;
    }


    //write result file
    public static void resultFileCreator(Map<String, String> result, String file) {

        String separator = System.getProperty("line.separator");

        try (FileWriter filewriter = new FileWriter(file + ".data")) {

            for (Map.Entry<String, String> entry : result.entrySet()) {
                filewriter.append(entry.getKey())
                        .append("|")
                        .append((entry.getValue()))
                        .append(separator);
            }


        } catch (IOException e) {
        }

    }

    public static void mapJoin(String transFile, String refFile, String regex, String outputPath){

        if (!Files.exists(Paths.get(outputPath + "/mappedTransactions"))) {
            new File(outputPath + "/mappedTransactions").mkdirs();
        }

        HashMap<Long, Double> refProduits = new HashMap<>();
        Set<Transaction> transactions = new HashSet<>();

        getStream(refFile).forEach(it -> {
            String[] s = it.split("\\|");
            refProduits.put(Long.parseLong(s[0]), Double.parseDouble(s[1]));
        });

        getStream(transFile).forEach(it -> {
            String[] s = it.split("\\|");
            Double price = 0.0;
            if (refProduits.get(Long.parseLong(s[3])) != null) {
                price = refProduits.get(Long.parseLong(s[3]));
            }
            transactions.add(new Transaction(s[1], Long.parseLong(s[3]), Long.parseLong(s[4]), price * Double.parseDouble(s[4])));
        });


        resultFileCreator(transactions, outputPath + "/mappedTransactions/transactions_" + getSubString(transFile, regex));
    }

    //write result file
    public static void resultFileCreator(Set<Transaction> transactions , String file) {

        String separator = System.getProperty("line.separator");

        try (FileWriter filewriter = new FileWriter(file + ".data")) {

            for( Transaction transaction : transactions){

                filewriter.append(transaction.getDatetime())
                        .append("|")
                        .append(transaction.getProduit().toString())
                        .append("|")
                        .append(transaction.getQuantite().toString())
                        .append("|")
                        .append(transaction.getPrix().toString())
                        .append(separator);

            }


        } catch (IOException e) {
        }

    }


    //compute the 100 best CA per store in the last seven days
    public static void getBestProductsCaPerStoreSevenDays(String transactionFile, String regex, String outputPath, String processDate) {

        Set<Transaction> transactions = new HashSet<>();
        HashMap<String, String> totalTurnoverByProduitSorted = new LinkedHashMap<>();


        getStream(transactionFile).forEach(it -> {
            String[] s = it.split("\\|");
            transactions.add(new Transaction(s[0], Long.parseLong(s[1]), Long.parseLong(s[2]),  Double.parseDouble(s[3])));
        });

        transactions
                .stream()
                .collect(Collectors.groupingBy(Transaction::getProduit,
                        Collectors.summingDouble(Transaction::getPrix)))
                .entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue()
                        .reversed()).limit(100).forEachOrdered(e -> totalTurnoverByProduitSorted.put(e.getKey().toString(), e.getValue().toString()));
        resultFileCreator(totalTurnoverByProduitSorted, outputPath + "/top_100_ca_" + getSubString(transactionFile, regex) + "_" + processDate + "-J7");
    }

}
