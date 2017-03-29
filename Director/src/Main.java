import creator.DataTableCreator;
import creator.DumpInstanceCreator;
import creator.DumpToDataTableCreator;
import extractor.CallGramExtractor;
import extractor.SequenceExtractor;
import model.data.DataTable;
import model.feature.CsvNumberRepresentation;
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
    private static final String datasetOutputPath = "C:\\Users\\yuval\\Dropbox\\Deeper\\Datasets Best Configurations\\";

    private static final String[] benignNames = { "Baseline", "Defrag", "Procmon", "Avast", "Wireshark" };
    private static final String[] maliciousNames = { "HiddenTear", "Cerber", "TeslaCrypt", "Vipasana", "Chimera"};

    private static final int sampleSize = 100;

    private static final Set<InstanceSetType> TRAIN_TEST = new LinkedHashSet<>(Arrays.asList(InstanceSetType.TRAIN_SET, InstanceSetType.TEST_SET));
    private static final Set<InstanceSetType> TRAIN = new LinkedHashSet<>(Arrays.asList(InstanceSetType.TRAIN_SET));
    private static final Set<InstanceSetType> TEST = new LinkedHashSet<>(Arrays.asList(InstanceSetType.TEST_SET));

    private static String tempSequenceType;
    private static String tempExperimentName;

    private static String tempTestedBenign;
    private static String tempTestedInfected;

    private static String tempSequenceLength;
    private static int tempMinSupport;
    private static int tempMaxSupport;

    private static String tempRanker;
    private static double tempRankThreshold;
    private static double tempCorrelationRatio;
    private static int tempNumberOfBins;
    private static String tempRepresentation;

    private static WekaEvaluator evaluator = new WekaEvaluator();

    public static void main(String[] args) throws IOException {
        int[] ns = {1, 2, 3, 4};
//        int[][] nss = {{1}, {2}, {3}, {4}, ns};
        int[][] nss = {ns};
        int minSequenceLength = 0;
        int maxSequenceLength = 4;
        int[] minSupports = {100};
        int maxSupport = 50000;
        int[] binsNumbers = new int[] {3, 4};
        double[] thresholds = {0, 0.5};
        double[] correlationRatios = {1, 0.9, 0.8};
        double trainPercentage = 0.8;
        BatchType batchType = BatchType.State_Batch;
        Map<String, List<String[]>> map = new LinkedHashMap<>();
        for (int minSupport : minSupports) {
            DataTable table = createDataTable(minSequenceLength, maxSequenceLength, minSupport, maxSupport, batchType);
            KnownMalwareDetection(table, trainPercentage, binsNumbers, thresholds, correlationRatios);
//            UnknownMalwareDetection(table, binsNumbers, thresholds, correlationRatios);
//            UnknownBenignDetection(table, binsNumbers, thresholds, correlationRatios);
//            map.putAll(UnknownBenignAndMalwareDetection(table, binsNumbers, thresholds, correlationRatios));
//            AnomalyDetection(table, binsNumbers, thresholds, correlationRatios);
            MalwareClassification(table, trainPercentage, binsNumbers, thresholds, correlationRatios);

//            for (int[] n : nss) {
//                table = createDataTable(n);
//                KnownMalwareDetection(table, trainPercentage, binsNumbers, thresholds, correlationRatios);
//                UnknownMalwareDetection(table, binsNumbers, thresholds, correlationRatios);
//                UnknownBenignDetection(table, binsNumbers, thresholds, correlationRatios);
//                UnknownBenignAndMalwareDetection(table, binsNumbers, thresholds, correlationRatios);
//                AnomalyDetection(table, binsNumbers, thresholds, correlationRatios);
//                MalwareClassification(table, trainPercentage, binsNumbers, thresholds, correlationRatios);
//            }
//
//            table = createDataTable(ns, minSequenceLength, maxSequenceLength, minSupport, maxSupport, batchType);
//            KnownMalwareDetection(table, trainPercentage, binsNumbers, thresholds, correlationRatios);
//            UnknownMalwareDetection(table, binsNumbers, thresholds, correlationRatios);
//            UnknownBenignDetection(table, binsNumbers, thresholds, correlationRatios);
//            UnknownBenignAndMalwareDetection(table, binsNumbers, thresholds, correlationRatios);
//            AnomalyDetection(table, binsNumbers, thresholds, correlationRatios);
//            MalwareClassification(table, trainPercentage, binsNumbers, thresholds, correlationRatios);
        }
//        int counter = 0;
//        for (String configuration : map.keySet()) {
//            counter++;
//            log.debug("Starting evaluating " + counter + " out of " + map.size() + " configurations.");
//            try {
//                evaluator.evaluateOnConfiguration(configuration, map.get(configuration));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        evaluator.printBest();
    }

    private static void createTimeSeriesDataSets() throws IOException {
        DataTable table = createDataTable(0, 4, 25, 200000, BatchType.State_Batch);
        DataTableCsvWriter writer = new DataTableCsvWriter();
        writer.dataTableToCsv(new DataTableToCsvRequest(table, datasetOutputPath + "Result.csv", CsvNumberRepresentation.Integer_Representation, TRAIN_TEST));
    }

    private static void AnomalyDetection(DataTable table, int[] binsNumbers, double[] thresholds, double[] correlationRatios) throws IOException {
        tempExperimentName = "Anomaly Detection";
        tempTestedBenign = "";
        tempTestedInfected = "";
        for (Instance instance : table.getInstances()) {
            if (instance.getType().equals("Baseline")) {
                instance.setClassification("Benign");
                table.put(instance, "Class", "Benign");
            } else {
                instance.setClassification("Anomaly");
                table.put(instance, "Class", "Anomaly");
            }
            instance.setSetType(InstanceSetType.TRAIN_SET);
        }
        outputDataTable(table, binsNumbers, thresholds, correlationRatios);
    }

    private static void MalwareClassification(DataTable table, double trainPercentage, int[] binsNumbers, double[] thresholds, double[] correlationRatios) throws IOException {
        tempExperimentName = "Malware Classification";
        tempTestedBenign = "";
        tempTestedInfected = "";
        Map<String, Integer> countMap = new HashMap<>();
        for (Instance instance : table.getInstances()) {
            String type = instance.getType();
            instance.setClassification(type);
            table.put(instance, "Class", type);
            countMap.putIfAbsent(type, 0);
            int count = countMap.get(type) + 1;
            countMap.put(type, count);
            if (count > trainPercentage * sampleSize) {
                instance.setSetType(InstanceSetType.TEST_SET);
            } else {
                instance.setSetType(InstanceSetType.TRAIN_SET);
            }
        }
        outputDataTable(table, binsNumbers, thresholds, correlationRatios);
    }

    private static void KnownMalwareDetection(DataTable table, double trainPercentage, int[] binsNumbers, double[] thresholds, double[] correlationRatios) throws IOException {
        tempExperimentName = "Known Malware Detection";
        tempTestedBenign = "";
        tempTestedInfected = "";
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
        outputDataTable(table, binsNumbers, thresholds, correlationRatios);
    }

    private static void UnknownMalwareDetection(DataTable table, int[] binsNumbers, double[] thresholds, double[] correlationRatios) throws IOException {
        tempExperimentName = "Unknown Malware Detection";
        tempTestedBenign = "";
        leaveOneOut(table, binsNumbers, thresholds, correlationRatios, maliciousNames, false);
    }

    private static void UnknownBenignDetection(DataTable table, int[] binsNumbers, double[] thresholds, double[] correlationRatios) throws IOException {
        tempExperimentName = "Unknown Benign Detection";
        tempTestedInfected = "";
        leaveOneOut(table, binsNumbers, thresholds, correlationRatios, benignNames, true);
    }

    private static Map<String, List<String[]>> UnknownBenignAndMalwareDetection(DataTable table, int[] binsNumbers, double[] thresholds, double[] correlationRatios) throws IOException {
        Map<String, List<String[]>> map = new LinkedHashMap<>();
        tempExperimentName = "Unknown Benign & Malware Detection";
        for (String benignToTest : benignNames) {
            for (String maliciousToTest : maliciousNames) {
                tempTestedBenign = benignToTest;
                tempTestedInfected = maliciousToTest;
                for (Instance instance : table.getInstances()) {
                    if (instance.getType().equals(benignToTest) | instance.getType().equals(maliciousToTest)) {
                        instance.setSetType(InstanceSetType.TEST_SET);
                    } else {
                        instance.setSetType(InstanceSetType.TRAIN_SET);
                    }
                }
                outputDataTable(table, binsNumbers, thresholds, correlationRatios);
//                for (String[] strings : outputDataTable(table, binsNumbers, thresholds, correlationRatios)) {
//                    map.putIfAbsent(strings[0], new ArrayList<>());
//                    map.get(strings[0]).add(new String[] {strings[1], strings[2]});
//                }
            }
        }
        return map;
    }

    private static void leaveOneOut(DataTable table, int[] binsNumbers, double[] thresholds, double[] correlationRatios, String[] typeNames, boolean benignTested) throws IOException {
        for (String typeName : typeNames) {
            if (benignTested) {
                tempTestedBenign = typeName;
            } else {
                tempTestedInfected = typeName;
            }
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
            outputDataTable(table, binsNumbers, thresholds, correlationRatios);
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
        tempMinSupport = minSupport;
        tempMaxSupport = maxSupport;
        log.info("Finished performing data table creation process.");
        return rankDataTable(table, new Ranker(new FishersScoreRanker()), 0, CsvNumberRepresentation.Integer_Representation, 1);
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

    private static List<String[]> outputDataTable(DataTable table, int[] binsNumbers, double[] thresholds, double[] correlationRatios) throws IOException {
        List<String[]> keyPath = new ArrayList<>();
        for (double threshold : thresholds) {
            for (double correlationRatio : correlationRatios) {
                Ranker ranker = new Ranker(new FishersScoreRanker());
                keyPath.addAll(rankAndWriteDataTable(table, ranker, threshold, correlationRatio));
                IDistanceMeasure[] measures = {
//                        new EntropyDistance(),
                        new CosineDistance(),
//                        new KullbackLeiblerDistance()
                };
                for (IDistanceMeasure measure : measures) {
                    for (int bins : binsNumbers) {
                        keyPath.add(discreteAndWriteDataTable(table, new TD4CDiscretizator(table.getInstances(), measure), bins, threshold, correlationRatio));
                    }
                }
            }
        }
        return keyPath;
    }

    private static String generateCsvPath(String postfix) {
        String filePath = String.join("\\", tempExperimentName, tempSequenceType,
                (tempTestedBenign.isEmpty() & tempTestedInfected.isEmpty() ? "All" :
                        tempTestedBenign.isEmpty() ? tempTestedInfected :
                        tempTestedInfected.isEmpty() ? tempTestedBenign :
                        tempTestedBenign + " & " + tempTestedInfected) + " Tested");
        String fileName = String.join("-", tempExperimentName, tempSequenceType,
                tempTestedBenign, tempTestedInfected, tempSequenceLength,
                String.valueOf(tempMinSupport), String.valueOf(tempMaxSupport), tempRanker,
                String.valueOf(tempCorrelationRatio), String.valueOf(tempRankThreshold), String.valueOf(tempNumberOfBins), tempRepresentation);
        return datasetOutputPath + filePath + "\\" + fileName + ((postfix != null) ? "_" + postfix : "") + ".csv";
    }

    private static String generateUniqueConfigurationKey() {
        return String.join("-", tempExperimentName, tempSequenceType,
                tempSequenceLength, String.valueOf(tempMinSupport), String.valueOf(tempMaxSupport), tempRanker,
                String.valueOf(tempCorrelationRatio), String.valueOf(tempRankThreshold), String.valueOf(tempNumberOfBins), tempRepresentation);
    }

    private static String[] discreteAndWriteDataTable(DataTable table, TD4CDiscretizator discretizator, int bins, double threshold, double correlationRatio) throws IOException {
        DataTable discreteTable = discreteDataTable(table, discretizator, bins, threshold, correlationRatio);
        return writeDataTable(discreteTable,CsvNumberRepresentation.Integer_Representation);
    }

    private static DataTable discreteDataTable(DataTable table, TD4CDiscretizator discretizator, int bins, double threshold, double correlationRatio) throws IOException {
        log.info("Going to discrete table with " + discretizator.getType() + " discretizator and " + bins + " bins.");
        DataTable discreteTable = discretizator.discrete(table, bins, threshold, correlationRatio);
        log.info("Finished discretion of table with " + discretizator.getType() + " discretizator and " + bins + " bins.");
        tempRanker = discretizator.getType();
        tempNumberOfBins = bins;
        tempRankThreshold = threshold;
        tempCorrelationRatio = correlationRatio;
        return discreteTable;
    }

    private static List<String[]> rankAndWriteDataTable(DataTable table, Ranker ranker, double threshold, double correlationRatio) throws IOException {
        List<String[]> keyPath = new ArrayList<>();
        DataTable rankedTable = rankDataTable(table, ranker, threshold, CsvNumberRepresentation.Binary_Representation, correlationRatio);
        keyPath.add(writeDataTable(rankedTable, CsvNumberRepresentation.Binary_Representation));
//        rankedTable = rankDataTable(table, ranker, threshold, CsvNumberRepresentation.Integer_Representation, correlationRatio);
//        keyPath.add(writeDataTable(rankedTable, CsvNumberRepresentation.Integer_Representation));
//        rankedTable = rankDataTable(table, ranker, threshold, CsvNumberRepresentation.TF_Representation, correlationRatio);
//        keyPath.add(writeDataTable(rankedTable, CsvNumberRepresentation.TF_Representation));
        rankedTable = rankDataTable(table, ranker, threshold, CsvNumberRepresentation.TFIDF_Representation, correlationRatio);
        keyPath.add(writeDataTable(rankedTable, CsvNumberRepresentation.TFIDF_Representation));
        return keyPath;
    }

    private static DataTable rankDataTable(DataTable table, Ranker ranker, double threshold, CsvNumberRepresentation representation, double correlationRatio) throws IOException {
        log.info("Going to rank table.");
        DataTable rankedTable = ranker.rankTable(table, threshold, representation, correlationRatio);
        log.info("Finished ranking table.");
        tempRanker = "Fisher's Score";
        tempNumberOfBins = 0;
        tempRankThreshold = threshold;
        tempCorrelationRatio = correlationRatio;
        return rankedTable;
    }

    private static String[] writeDataTable(DataTable table, CsvNumberRepresentation representation) throws IOException {
        tempRepresentation = representation.name().replace("_", " ");
//        String trainPath = generateCsvPath("Train");
//        String testPath = generateCsvPath("Test");
        String path = generateCsvPath("");
        String key = generateUniqueConfigurationKey();
        log.info("Going to write table to path " + path + ".");
        DataTableCsvWriter writer = new DataTableCsvWriter();
        writer.dataTableToCsv(new DataTableToCsvRequest(table, path, representation, TRAIN_TEST));
//        writer.dataTableToCsv(new DataTableToCsvRequest(table, trainPath, representation, TRAIN));
//        writer.dataTableToCsv(new DataTableToCsvRequest(table, testPath, representation, TEST));
        log.info("Finished writing table to path " + path + ".");
        return new String[] {key, path};
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
//        creator.addExtractor(new DumpStateExtractor());
//        creator.addExtractor(new DumpTimestampExtractor());
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
