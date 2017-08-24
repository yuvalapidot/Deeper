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
import writer.DataTableCsvWriter;
import writer.DataTableToCsvRequest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class TransferLearningMain {

    private static final Logger log = LogManager.getLogger(TransferLearningMain.class);

    private static final String jsonsTrainDirectoryPath = "D:\\Dropbox\\Deeper\\All Jsons\\_1_IIS_Baseline1";
    private static final String jsonsTestDirectoryPath = "D:\\Dropbox\\Deeper\\All Jsons\\_4_Email_Baseline1";
    private static final String datasetOutputPath = "D:\\Dropbox\\Deeper\\All Datasets\\Transfer_Learning_1_to_4\\";

    private static final String[] benignNames = { "Avast", "Defrag", "Procmon", "Perfmon", "Wireshark", "Procexp" };
    private static final String[] maliciousNames = { "HiddenTear", "Cerber", "TeslaCrypt", "Vipasana", "Chimera" };

    private static final Set<InstanceSetType> TRAIN_TEST = new LinkedHashSet<>(Arrays.asList(InstanceSetType.TRAIN_SET, InstanceSetType.TEST_SET));

    private static int minSequenceLength = 0;
    private static int maxSequenceLength = 3;
    private static int minSupport = 100;
    private static int maxSupport = 50000;
    private static double threshold = 0;
    private static double correlationRatio = 1;

    private static int samplesMaxSize = 100;

    public static void main(String[] args) throws IOException {
        DataTable table = createDataTable();
        writeDataTable(table, CsvNumberRepresentation.Integer_Representation, datasetOutputPath + "Integer.csv");
        DataTable binaryRankedTable = new Ranker(new FishersScoreRanker()).rankTable(table, threshold, CsvNumberRepresentation.Binary_Representation, correlationRatio);
        writeDataTable(binaryRankedTable, CsvNumberRepresentation.Binary_Representation, datasetOutputPath + "Binary.csv");
        DataTable tfRankedTable = new Ranker(new FishersScoreRanker()).rankTable(table, threshold, CsvNumberRepresentation.TF_Representation, correlationRatio);
        writeDataTable(tfRankedTable, CsvNumberRepresentation.TF_Representation, datasetOutputPath + "TF.csv");
        DataTable tfidfRankedTable = new Ranker(new FishersScoreRanker()).rankTable(table, threshold, CsvNumberRepresentation.TFIDF_Representation, correlationRatio);
        writeDataTable(tfidfRankedTable, CsvNumberRepresentation.TFIDF_Representation, datasetOutputPath + "TFIDF.csv");
        System.out.println("DONE");
    }

    private static DataTable createDataTable() throws IOException {
        Map<String, List<DumpInstance>> instanceMap = getDumpsInstances(getDumps(getJsonFiles(new String[] {jsonsTrainDirectoryPath, jsonsTestDirectoryPath})));

        log.info("Starting data table creation process.");
        DataTable table = getSequenceDataTable(instanceMap);
        log.info("Finished performing data table creation process.");
        addClassificationToTable(instanceMap, table);
        return rankDataTable(table, new Ranker(new FishersScoreRanker()), threshold, CsvNumberRepresentation.Integer_Representation, correlationRatio);
    }

    private static void addClassificationToTable(Map<String, List<DumpInstance>> instanceMap, DataTable table) {
        for (String instancesKey : instanceMap.keySet()){
            for (DumpInstance instance : instanceMap.get(instancesKey)) {
                table.put(instance, "Class", instance.getClassification());
                table.put(instance, "Scenario", instancesKey);
            }
        }
    }

    private static DataTable getSequenceDataTable(Map<String, List<DumpInstance>> instances) {
        SequenceExtractor extractor = new SequenceExtractor(minSupport, maxSupport, minSequenceLength, maxSequenceLength, 100);
        extractor.setUseBatchSizeForSupport(true);
        extractor.setInstances(instances);
        return extractor.extract();
    }

    private static DataTable rankDataTable(DataTable table, Ranker ranker, double threshold, CsvNumberRepresentation representation, double correlationRatio) throws IOException {
        log.info("Going to rank table.");
        DataTable rankedTable = ranker.rankTable(table, threshold, representation, correlationRatio);
        log.info("Finished ranking table.");
        return rankedTable;
    }

    private static Map<String, List<File>> getJsonFiles(String[] mainDirectories) throws IOException {
        Map<String, List<File>> fileMap = new HashMap<>();
        for (String mainDirectory : mainDirectories) {
            log.info("Going to get all json files from " + mainDirectory + ".");
            List<Path> directories = new ArrayList<>();
            Files.walk(Paths.get(mainDirectory)).filter(filePath -> Files.isDirectory(filePath)).forEach(directories::add);
            for (Path directory : directories) {
                if (!directory.toString().equals(jsonsTrainDirectoryPath) & !directory.toString().equals(jsonsTestDirectoryPath)) {
                    List<File> files = new ArrayList<>();
                    Files.walk(directory).filter(filePath -> Files.isRegularFile(filePath)).forEach(filePath -> files.add(filePath.toFile()));
                    fileMap.put(directory.toString(), files);
                }
            }

        }
        log.info("Finished getting all json files, " + fileMap.size() + " directories were read from.");
        return fileMap;
    }

    private static Map<String, List<Dump>> getDumps(Map<String, List<File>> files) {
        log.info("Going to get all dumps from " + files.size() + " folders.");
        JsonDumpReader reader = new JsonDumpReader();
        Map<String, List<Dump>> dumps = new HashMap<>();
        for (String folderName : files.keySet()) {
            List<JsonToDumpRequest> requests = files.get(folderName).stream().map(file -> new JsonToDumpRequest(file)).collect(Collectors.toList());
            dumps.put(folderName, reader.jsonsToDumps(requests));
        }
        log.info("Finished getting all dumps, " + dumps.size() + " dumps created.");
        return dumps;
    }

    private static Map<String, List<DumpInstance>> getDumpsInstances(Map<String, List<Dump>> dumps) {
        Map<String, List<DumpInstance>> dumpInstances = new HashMap<>();
        List<Instance> instances = new ArrayList<>();
        for (String folderName : dumps.keySet()) {
            dumpInstances.put(folderName, new ArrayList<>());
            InstanceSetType setType = getFolderSetType(folderName);
            String classification = getFolderClassification(folderName);
            for (Dump dump : dumps.get(folderName)) {
                DumpInstance instance = new DumpInstance(dump, setType, classification);
                dumpInstances.get(folderName).add(instance);
                instances.add(instance);
            }
        }
        Feature.initialize(instances);
        return dumpInstances;
    }

    private static String getFolderClassification(String folderName) {
        for (String maliciousName : maliciousNames) {
            if (folderName.contains(maliciousName)) {
                return "MALICIOUS";
            }
        }
        for (String benignName : benignNames) {
            if (folderName.contains(benignName)) {
                return "BENIGN";
            }
        }
        log.warn(folderName + " classification was set to BENIGN by default");
        return "BENIGN";
    }

    private static InstanceSetType getFolderSetType(String folderName) {
        if (folderName.contains(jsonsTrainDirectoryPath)) {
            return InstanceSetType.TRAIN_SET;
        } else if (folderName.contains(jsonsTestDirectoryPath)) {
            return InstanceSetType.TEST_SET;
        }
        log.warn("No set type was found for " + folderName + ", setting to TRAIN");
        return InstanceSetType.TRAIN_SET;
    }

    private static void writeDataTable(DataTable table, CsvNumberRepresentation representation, String path) throws IOException {
        log.info("Going to write table to path " + path + ".");
        DataTableCsvWriter writer = new DataTableCsvWriter();
        writer.dataTableToCsv(new DataTableToCsvRequest(table, path, representation, TRAIN_TEST));
        log.info("Finished writing table to path " + path + ".");
    }
}
