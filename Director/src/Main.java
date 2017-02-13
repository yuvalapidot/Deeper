import creator.DataTableCreator;
import creator.DumpInstanceCreator;
import creator.DumpToDataTableCreator;
import extractor.CallGramExtractor;
import extractor.SequenceExtractor;
import model.data.DataTable;
import model.feature.Feature;
import model.instance.DumpInstance;
import model.instance.Instance;
import model.instance.InstanceSetType;
import model.memory.Dump;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ranker.Ranker;
import ranker.rankers.FishersScoreRanker;
import reader.JsonDumpReader;
import reader.JsonToDumpRequest;
import td4c.TD4CDiscretizator;
import td4c.measures.CosineDistance;
import td4c.measures.EntropyDistance;
import td4c.measures.IDistanceMeasure;
import td4c.measures.KullbackLeiblerDistance;
import model.feature.CsvNumberRepresentation;
import writer.DataTableCsvWriter;
import writer.DataTableToCsvRequest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    private static final Logger log = LogManager.getLogger(Main.class);

    private static final String jsonsDirectoryPath = "C:\\Users\\yuval\\Dropbox\\Deeper\\Jsons";
    private static final String datasetOutputPath = "C:\\Users\\yuval\\Dropbox\\Deeper\\Datasets\\";

    private static final String[] benignNames = { "Baseline", "Defrag", "Procmon", "Avast", "Wireshark" };
    private static final String[] maliciousNames = { "HiddenTear", "Cerber", "TeslaCrypt", "Vipasana", "Chimera"};

    private static final int sampleSize = 100;

    private static final Set<InstanceSetType> TRAIN_TEST = new LinkedHashSet<>(Arrays.asList(InstanceSetType.TRAIN_SET, InstanceSetType.TEST_SET));

    private static String tempSequenceType;
    private static String tempArgumentConsideration;
    private static String tempExperimentName;

    private static String tempFirstTested;
    private static String tempSecondTested;

    private static String tempBatchType;
    private static String tempSequenceLength;
    private static int tempMinSupport;
    private static int tempMaxSupport;

    private static String tempRanker;
    private static double tempRankThreshold;
    private static int tempNumberOfBins;
    private static String tempRepresentation;

    public static void main(String[] args) throws IOException {
        tempArgumentConsideration = "No Argument Consideration";
        int[] ns = {1, 2, 3, 4};
        int[][] nss = {{1}, {2}, {3}, {4}, ns};
        int minSequencelength = 0;
        int maxSequencelength = 4;
        int minSupport = 100;
        int maxSupport = 5000;
        double trainPercentage = 0.8;
        int[] binsNumbers = new int[] {2, 3, 4, 5, 6, 7};
        double threshold = 0.05;
        BatchType batchType = BatchType.State_Batch;
        for (int[] n : nss) {
            DataTable table = createDataTable(n);
            experiment1(table, trainPercentage, binsNumbers, threshold);
            experiment2(table, binsNumbers, threshold);
            experiment3(table, binsNumbers, threshold);
        }
        DataTable table = createDataTable(minSequencelength, maxSequencelength, minSupport, maxSupport, batchType);
        experiment1(table, trainPercentage, binsNumbers, threshold);
        experiment2(table, binsNumbers, threshold);
        experiment3(table, binsNumbers, threshold);
        table = createDataTable(ns, minSequencelength, maxSequencelength, minSupport, maxSupport, batchType);
        experiment1(table, trainPercentage, binsNumbers, threshold);
        experiment2(table, binsNumbers, threshold);
        experiment3(table, binsNumbers, threshold);
    }

    private static void experiment1(DataTable table, double trainPercentage, int[] binsNumbers, double threshold) throws IOException {
        tempExperimentName = "Test All";
        tempFirstTested = "";
        tempSecondTested = "";
        Map<String, Integer> countMap = new HashMap<>();
        for (Instance instance : table.getInstances()) {
            String type = instance.getType();
            countMap.putIfAbsent(type, 0);
            int count = countMap.get(type) + 1;
            countMap.put(type, count);
            if (count > trainPercentage * sampleSize) {
                instance.setSetType(InstanceSetType.TEST_SET);
            } else {
                instance.setSetType(InstanceSetType.TRAIN_SET);
            }
        }
        outputDataTable(table, binsNumbers, threshold);
    }

    private static void experiment2(DataTable table, int[] binsNumbers, double threshold) throws IOException {
        tempExperimentName = "Leave One Out";
        tempSecondTested = "";
        leaveOneOut(table, binsNumbers, threshold, benignNames);
        leaveOneOut(table, binsNumbers, threshold, maliciousNames);
    }

    private static void experiment3(DataTable table, int[] binsNumbers, double threshold) throws IOException {
        tempExperimentName = "Leave Two Out";
        for (String benignToTest : benignNames) {
            for (String maliciousToTest : maliciousNames) {
                tempFirstTested = benignToTest;
                tempSecondTested = maliciousToTest;
                for (Instance instance : table.getInstances()) {
                    if (instance.getType().equals(benignToTest) | instance.getType().equals(maliciousToTest)) {
                        instance.setSetType(InstanceSetType.TEST_SET);
                    } else {
                        instance.setSetType(InstanceSetType.TRAIN_SET);
                    }
                }
                outputDataTable(table, binsNumbers, threshold);
            }
        }
    }

    private static void leaveOneOut(DataTable table, int[] binsNumbers, double threshold, String[] typeNames) throws IOException {
        for (String typeName : typeNames) {
            tempFirstTested = typeName;
            String classification = "Unknown";
            // Declare out as Test set type.
            for (Instance instance : table.getInstances()) {
                if (instance.getType().equals(typeName)) {
                    instance.setSetType(InstanceSetType.TEST_SET);
                    classification = instance.getClassification();
                }
            }
            // Declare rest set type.
            Map<String, Integer> countMap = new HashMap<>();
            for (Instance instance : table.getInstances()) {
                // Declare same classification as Train set type.
                if (instance.getClassification().equals(classification)) {
                    if (!instance.getType().equals(typeName)) {
                        instance.setSetType(InstanceSetType.TRAIN_SET);
                    }
                } else {
                    String type = instance.getType();
                    countMap.putIfAbsent(type, 0);
                    int count = countMap.get(type) + 1;
                    countMap.put(instance.getType(), count);
                    // Declare opposite classification as Train set type and Test set type.
                    if (count > (1 - (1.0 / typeNames.length)) * sampleSize) {
                        instance.setSetType(InstanceSetType.TEST_SET);
                    } else {
                        instance.setSetType(InstanceSetType.TRAIN_SET);
                    }
                }
            }
            outputDataTable(table, binsNumbers, threshold);
        }
    }

    private static DataTable createDataTable(int[] ns) throws IOException {
        return createDataTable(ns, 0, 0, 0, 0, BatchType.Single_Batch);
    }

    private static DataTable createDataTable(int minSequenceLength, int maxSequenceLength, int minSupport, int maxSupport, BatchType batch) throws IOException {
        return createDataTable(null, minSequenceLength, maxSequenceLength, minSupport, maxSupport, batch);
    }

    private static DataTable createDataTable(int[] ns, int minSequenceLength, int maxSequenceLength, int minSupport, int maxSupport, BatchType batch) throws IOException {
        return createDataTable(jsonsDirectoryPath, benignNames, maliciousNames, sampleSize, ns, minSequenceLength, maxSequenceLength, minSupport, maxSupport, batch);
    }

    private static DataTable createDataTable(String jsonsDirectoryPath, String[] benignNames, String[] maliciousNames, int sampleSize, int[] ns, int minSequenceLength, int maxSequenceLength, int minSupport, int maxSupport, BatchType batch) throws IOException {
        log.info("Starting performing data table creation process...");
        DumpInstanceCreator[] creators = getCreators(benignNames, maliciousNames, sampleSize);
        List<Dump> dumps = getDumps(getJsonFiles(jsonsDirectoryPath));
        List<DumpInstance> instances = getDumpInstances(dumps, creators);
        DataTable table;
        if (ns == null) {
            tempSequenceLength = String.valueOf(minSequenceLength) + ((minSequenceLength == maxSequenceLength) ? "" : " to " + String.valueOf(maxSequenceLength));
            table = getSequenceDataTable(instances, minSequenceLength, maxSequenceLength, minSupport, maxSupport, batch);
        } else if (maxSequenceLength <= 0) {
            int min = min(ns);
            int max = max(ns);
            tempSequenceLength = String.valueOf(min) + ((min == max) ? "" : " to " + String.valueOf(max));
            table = getNGramDataTable(instances, ns);
        } else {
            int min = Math.min(min(ns), minSequenceLength);
            int max = Math.max(max(ns), maxSequenceLength);
            tempSequenceLength = String.valueOf(min) + ((min == max) ? "" : " to " + String.valueOf(max));
            table = getHybridDataTable(instances, ns, minSequenceLength, maxSequenceLength, minSupport, maxSupport, batch);
        }
        tempBatchType = batch.name().replace("_", " ");
        tempMinSupport = minSupport;
        tempMaxSupport = maxSupport;
        log.info("Finished performing data table creation process.");
        return table;
    }

    private static int min(int[] arr) {
        int min = Integer.MAX_VALUE;
        for (int i : arr) {
            if (min > i) {
                min = i;
            }
        }
        return min;
    }

    private static int max(int[] arr) {
        int max = Integer.MIN_VALUE;
        for (int i : arr) {
            if (max < i) {
                max = i;
            }
        }
        return max;
    }

    private static void outputDataTable(DataTable table, int[] binsNumbers, double threshold) throws IOException {
        Ranker ranker = new Ranker(new FishersScoreRanker());
        rankAndWriteDataTable(table, ranker, threshold);
        IDistanceMeasure[] measures = {new EntropyDistance(), new CosineDistance(), new KullbackLeiblerDistance()};
        for (IDistanceMeasure measure : measures) {
            for (int bins : binsNumbers) {
                discreteAndWriteDataTable(table, new TD4CDiscretizator(table.getInstances(), measure), bins, threshold);
            }
        }
    }

    private static String generateCsvPath() {
        String filePath = String.join("\\", tempSequenceType, tempArgumentConsideration, tempExperimentName,
                tempFirstTested.equals("") ? "All Tested" : tempFirstTested + (tempSecondTested.equals("") ? " Tested" : " & " + tempSecondTested + " Tested"),
                tempBatchType, "Sequence Length " + tempSequenceLength,
                "Support " + String.valueOf(tempMinSupport) + ((tempMinSupport == tempMaxSupport) ? "" :" to " + String.valueOf(tempMaxSupport)),
                tempRanker, String.valueOf(tempNumberOfBins) + " Bins", tempRepresentation.replace("_", " "));
        String fileName = String.join("-", tempSequenceType, tempArgumentConsideration, tempExperimentName,
                tempFirstTested, tempSecondTested, tempBatchType, tempSequenceLength,
                String.valueOf(tempMinSupport), String.valueOf(tempMaxSupport), tempRanker,
                String.valueOf(tempRankThreshold), String.valueOf(tempNumberOfBins), tempRepresentation);
        return datasetOutputPath + filePath + "\\" + fileName + ".csv";
    }

    private static void discreteAndWriteDataTable(DataTable table, TD4CDiscretizator discretizator, int bins, double threshold) throws IOException {
        DataTable discreteTable = discreteDataTable(table, discretizator, bins, threshold);
        writeDataTable(discreteTable,CsvNumberRepresentation.Integer_Representation);
    }

    private static DataTable discreteDataTable(DataTable table, TD4CDiscretizator discretizator, int bins, double threshold) throws IOException {
        log.info("Going to discrete table with " + discretizator.getType() + " discretizator and " + bins + " bins.");
        DataTable discreteTable = discretizator.discrete(table, bins, threshold);
        log.info("Finished discretion of table with " + discretizator.getType() + " discretizator and " + bins + " bins.");
        tempRanker = discretizator.getType();
        tempNumberOfBins = bins;
        tempRankThreshold = threshold;
        return discreteTable;
    }

    private static void rankAndWriteDataTable(DataTable table, Ranker ranker, double threshold) throws IOException {
        DataTable rankedTable = rankDataTable(table, ranker, threshold, CsvNumberRepresentation.Binary_Representation);
        writeDataTable(rankedTable, CsvNumberRepresentation.Binary_Representation);
        rankedTable = rankDataTable(table, ranker, threshold, CsvNumberRepresentation.Integer_Representation);
        writeDataTable(rankedTable, CsvNumberRepresentation.Integer_Representation);
        rankedTable = rankDataTable(table, ranker, threshold, CsvNumberRepresentation.TF_Representation);
        writeDataTable(rankedTable, CsvNumberRepresentation.TF_Representation);
        rankedTable = rankDataTable(table, ranker, threshold, CsvNumberRepresentation.TFIDF_Representation);
        writeDataTable(rankedTable, CsvNumberRepresentation.TFIDF_Representation);
    }

    private static DataTable rankDataTable(DataTable table, Ranker ranker, double threshold, CsvNumberRepresentation representation) throws IOException {
        log.info("Going to rank table.");
        DataTable rankedTable = ranker.rankTable(table, threshold, representation);
        log.info("Finished ranking table.");
        tempRanker = "Fisher's Score";
        tempNumberOfBins = 0;
        tempRankThreshold = threshold;
        return rankedTable;
    }

    private static void writeDataTable(DataTable table, CsvNumberRepresentation representation) throws IOException {
        tempRepresentation = representation.name().replace("_", " ");
        String path = generateCsvPath();
        log.info("Going to write table to path " + path + ".");
        DataTableCsvWriter writer = new DataTableCsvWriter();
        writer.dataTableToCsv(new DataTableToCsvRequest(table, path, representation, TRAIN_TEST));
        log.info("Finished writing table to path " + path + ".");
    }

    private static DataTable getNGramDataTable(List<DumpInstance> instances, int[] ns) {
        tempSequenceType = "NGram";
        return getDataTable(instances, ns, 0, 0, 0, 0, BatchType.Single_Batch);
    }

    private static DataTable getSequenceDataTable(List<DumpInstance> instances, int minSequenceLength, int maxSequenceLength, int minSupport, int maxSupport, BatchType batch) {
        tempSequenceType = "Sequence";
        return getDataTable(instances, null, minSequenceLength, maxSequenceLength, minSupport, maxSupport, batch);
    }

    private static DataTable getHybridDataTable(List<DumpInstance> instances, int[] ns, int minSequenceLength, int maxSequenceLength, int minSupport, int maxSupport, BatchType batch) {
        tempSequenceType = "Hybrid";
        return getDataTable(instances, ns, minSequenceLength, maxSequenceLength, minSupport, maxSupport, batch);
    }

    private static DataTable getDataTable(List<DumpInstance> instances, int[] ns, int minSequenceLength, int maxSequenceLength, int minSupport, int maxSupport, BatchType batch) {
        log.info("Going to generate " + tempSequenceType + " data table.");
        DataTableCreator creator = new DumpToDataTableCreator(instances);
        if (ns != null) {
            for (int i : ns) {
                creator.addExtractor(new CallGramExtractor(i));
            }
        }
        if (maxSequenceLength > 0) {
            creator.addExtractor(new SequenceExtractor(minSupport, maxSupport, minSequenceLength, maxSequenceLength, batch.getSize()));
        }
        DataTable table = creator.createDataTable();
        log.info("Finished generating " + tempSequenceType + " data table.");
        return table;
    }

    private static DumpInstanceCreator[] getCreators(String[] benignNames, String[] maliciousNames, int sampleSize) {
        log.info("Going to generate creators to receive all instances.");
        DumpInstanceCreator[] creators = new DumpInstanceCreator[benignNames.length + maliciousNames.length];
        for (int i = 0; i < maliciousNames.length; i++) {
            creators[i] = new DumpInstanceCreator(maliciousNames[i], "MALICIOUS", sampleSize, 100);
        }
        for (int i = 0; i < benignNames.length; i++) {
            creators[maliciousNames.length + i] = new DumpInstanceCreator(benignNames[i], "BENIGN", sampleSize, 100);
        }
        log.info("Finished generating creators, " + creators.length + " creators were generated." );
        return creators;
    }

    private static List<File> getJsonFiles(String jsonsDirectoryPath) throws IOException {
        log.info("Going to get all json files from " + jsonsDirectoryPath + ".");
        List<File> files = new ArrayList<>();
        Files.walk(Paths.get(jsonsDirectoryPath)).filter(filePath -> Files.isRegularFile(filePath)).forEach(filePath -> files.add(filePath.toFile()));
        log.info("Finished getting all json files, " + files.size() + " files were read.");
        return files;
    }

    private static List<Dump> getDumps(List<File> files) {
        log.info("Going to get all dumps from " + files.size() + " files.");
        JsonDumpReader reader = new JsonDumpReader();
        List<JsonToDumpRequest> requests = files.stream().map(file -> new JsonToDumpRequest(file)).collect(Collectors.toList());
        List<Dump> dumps = reader.jsonsToDumps(requests);
        log.info("Finished getting all dumps, " + dumps.size() + " dumps created.");
        return dumps;
    }

    private static List<DumpInstance> getDumpInstances(List<Dump> dumps, DumpInstanceCreator[] creators) {
        log.info("Going to create dump instances out of " + dumps.size() + " dumps and " + creators.length + " creators.");
        List<DumpInstance> instances = new ArrayList<>();
        for (DumpInstanceCreator creator : creators) {
            int timestamp = 0;
            for (Dump dump : dumps) {
                DumpInstance instance = creator.create(dump);
                if (instance != null) {
                    instance.setTimestamp(timestamp++);
                    instances.add(instance);
                }
            }
        }
        Feature.initialize(instances);
        log.info("Finished creating dump instances, " + instances.size() + " instances were created.");
        return instances;
    }

    private enum BatchType {
        Single_Batch(1),
        State_Batch(100),
        Class_Batch(500),
        Whole_Batch(1000);

        private final int size;

        BatchType(int size) {
            this.size = size;
        }

        public int getSize() {
            return size;
        }
    }
}
