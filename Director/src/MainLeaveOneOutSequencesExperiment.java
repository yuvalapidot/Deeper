import creator.DataTableCreator;
import creator.DumpToDataTableCreator;
import extractor.IFeatureExtractor;
import extractor.SequenceExtractor;
import model.data.DataTable;
import model.instance.DumpInstance;
import model.instance.InstanceSetType;
import model.memory.Dump;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reader.JsonDumpReader;
import reader.JsonToDumpRequest;
import writer.DataTableCsvWriter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class MainLeaveOneOutSequencesExperiment {

    private static final Logger log = LogManager.getLogger(MainLeaveOneOutSequencesExperiment.class);

    private static final int[] minimumSupports = {1000};
    private static final int maximumSupport = 10000;
    private static final int minimumSequenceLength = 4;
    private static final int maximumSequenceLength = 4;
    private static final int batchSize = 100;

    private static final String jsonsDirectoryPath = "D:\\Dropbox\\NGrams\\Jsons";
    private static final String csvPath = "D:\\Dropbox\\NGrams\\Results-Sequence\\";
    private static final String csvName = "-sequence.csv";

    private static final Set<InstanceSetType> TRAIN_TEST = new HashSet<>(Arrays.asList(InstanceSetType.TRAIN_SET, InstanceSetType.TEST_SET));

    private static final int DumpsCount = 100;

    private static final String[] BenignNames = {"Baseline", "Procmon", "Avast", "Wireshark", "Defrag"};
    private static final String[] MaliciousNames = {"HiddenTear", "Cerber", "TeslaCrypt", "Vipasana", "Chimera"};

    public static void main(String[] args) throws IOException {
        DataTableCsvWriter writer = new DataTableCsvWriter();
        List<Dump> dumps = getDumps(getJsonFiles());
        for (String benignName : BenignNames) {
            for (String maliciousName : MaliciousNames) {
                DumpInstanceCreator[] creators = new DumpInstanceCreator[BenignNames.length + MaliciousNames.length];
                for (int i = 0; i < BenignNames.length; i++) {
                    if (!BenignNames[i].equals(benignName)) {
                        creators[i] = new DumpInstanceCreator(BenignNames[i], "BENIGN", DumpsCount, 100);
                    } else {
                        creators[i] = new DumpInstanceCreator(BenignNames[i], "BENIGN", DumpsCount, 0);
                    }
                }
                for (int i = 0; i < MaliciousNames.length; i++) {
                    if (!MaliciousNames[i].equals(maliciousName)) {
                        creators[BenignNames.length + i] = new DumpInstanceCreator(MaliciousNames[i], "MALICIOUS", DumpsCount, 100);
                    } else {
                        creators[BenignNames.length + i] = new DumpInstanceCreator(MaliciousNames[i], "MALICIOUS", DumpsCount, 0);
                    }
                }
                List<DumpInstance> dumpInstances = getDumpInstances(dumps, creators);
                String experimentName = benignName + "+" + maliciousName + "-Out";
                int trainCounter = 0;
                for (DumpInstance instance : dumpInstances) {
                    if (instance.getSetType().equals(InstanceSetType.TRAIN_SET)) {
                        trainCounter++;
                    }
                }
                log.info("Experiment " + experimentName + " train percentage: " + (((double) trainCounter) / dumpInstances.size()) * 100);
                for (int j : minimumSupports) {
                    IFeatureExtractor<DumpInstance> extractor = new SequenceExtractor(j, maximumSupport, minimumSequenceLength, maximumSequenceLength, batchSize);
                    DataTableCreator creator = new DumpToDataTableCreator(dumpInstances);
                    creator.addExtractor(extractor);
                    DataTable table = creator.createDataTable();
//                    writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + experimentName + "\\" + "Regular\\" + experimentName + "-" + j + "-regular" + csvName, CsvNumberRepresentation.INTEGER_REPRESENTATION, TRAIN_TEST, 100, , 0.1));
//                    writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + experimentName + "\\" + "Binary\\" + experimentName + "-" + j + "-binary" + csvName, CsvNumberRepresentation.BINARY_REPRESENTATION, TRAIN_TEST, 100, 0.1));
//                    writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + experimentName + "\\" + "TF\\" + experimentName + "-" + j + "-tf" + csvName, CsvNumberRepresentation.TF_REPRESENTATION, TRAIN_TEST, 100, 0.1));
//                    writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + experimentName + "\\" + "TF-IDF\\" + experimentName + "-" + j + "-tf-idf" + csvName, CsvNumberRepresentation.TFIDF_REPRESENTATION, TRAIN_TEST, 100, 0.1));
                }
            }
        }
    }

    private static List<File> getJsonFiles() throws IOException {
        List<File> files = new ArrayList<>();
        Files.walk(Paths.get(jsonsDirectoryPath)).filter(filePath -> Files.isRegularFile(filePath)).forEach(filePath -> files.add(filePath.toFile()));
        return files;
    }

    private static List<Dump> getDumps(List<File> files) {
        JsonDumpReader reader = new JsonDumpReader();
        List<JsonToDumpRequest> requests = files.stream().map(file -> new JsonToDumpRequest(file)).collect(Collectors.toList());
        return reader.jsonsToDumps(requests);
    }

    private static List<DumpInstance> getDumpInstances(List<Dump> dumps, DumpInstanceCreator[] creators) {
        List<DumpInstance> instances = new ArrayList<>();
        for (DumpInstanceCreator creator : creators) {
            for (Dump dump : dumps) {
                DumpInstance instance = creator.create(dump);
                if (instance != null) {
                    instances.add(instance);
                }
            }
        }
        return instances;
    }
}
