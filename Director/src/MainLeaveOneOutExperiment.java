import creator.DataTableCreator;
import creator.DumpToDataTableCreator;
import extractor.CallGramExtractor;
import extractor.IFeatureExtractor;
import model.data.DataTable;
import model.instance.DumpInstance;
import model.instance.InstanceSetType;
import model.memory.Dump;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reader.JsonDumpReader;
import reader.JsonToDumpRequest;
import td4c.TD4CDiscretizator;
import td4c.measures.CosineDistance;
import td4c.measures.EntropyDistance;
import td4c.measures.KullbackLeiblerDistance;
import writer.CsvNumberRepresentation;
import writer.DataTableCsvWriter;
import writer.DataTableToCsvRequest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class MainLeaveOneOutExperiment {

    private static final Logger log = LogManager.getLogger(MainLeaveOneOutExperiment.class);

    private static final int upToN = 5;
    private static final String jsonsDirectoryPath = "D:\\Dropbox\\NGrams\\Jsons";
    private static final String csvPath = "D:\\Dropbox\\NGrams\\Results\\";
    private static final String csvName = "-call-gram.csv";

    private static final Set<InstanceSetType> TRAIN_TEST = new LinkedHashSet<>(Arrays.asList(InstanceSetType.TRAIN_SET, InstanceSetType.TEST_SET));

    private static final int DumpsCount = 100;

    private static final String[] BenignNames = {"Baseline", "Procmon", "Avast", "Wireshark", "Defrag"};
    private static final String[] MaliciousNames = {"HiddenTear", "Cerber", "TeslaCrypt", "Vipasana", "Chimera"};

    public static void main(String[] args) throws IOException {
        DataTableCsvWriter writer = new DataTableCsvWriter();
        List<Dump> dumps = getDumps(getJsonFiles());
        for (String benignName : BenignNames) {
            for (String maliciousName : MaliciousNames) {
                DumpInstanceCreator[] creators = new DumpInstanceCreator[BenignNames.length + MaliciousNames.length];
                for (int i = 0; i < MaliciousNames.length; i++) {
                    if (!MaliciousNames[i].equals(maliciousName)) {
                        creators[i] = new DumpInstanceCreator(MaliciousNames[i], "MALICIOUS", DumpsCount, 100);
                    } else {
                        creators[i] = new DumpInstanceCreator(MaliciousNames[i], "MALICIOUS", DumpsCount, 0);
                    }
                }
                for (int i = 0; i < BenignNames.length; i++) {
                    if (!BenignNames[i].equals(benignName)) {
                        creators[MaliciousNames.length + i] = new DumpInstanceCreator(BenignNames[i], "BENIGN", DumpsCount, 100);
                    } else {
                        creators[MaliciousNames.length + i] = new DumpInstanceCreator(BenignNames[i], "BENIGN", DumpsCount, 0);
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
//                DataTable[] tables = new DataTable[upToN];
                for (int j = 1; j <= upToN; j++) {
                    IFeatureExtractor<DumpInstance> extractor = new CallGramExtractor(j);
                    DataTableCreator creator = new DumpToDataTableCreator(dumpInstances);
                    creator.addExtractor(extractor);
                    DataTable table = creator.createDataTable();
                    TD4CDiscretizator klDiscretizator = new TD4CDiscretizator(table.getInstances(), new KullbackLeiblerDistance());
                    DataTable klTable3 = klDiscretizator.discrete(table, 3, 1);
                    DataTable klTable5 = klDiscretizator.discrete(table, 5, 1);
                    TD4CDiscretizator entropyDiscretizator = new TD4CDiscretizator(table.getInstances(), new EntropyDistance());
                    DataTable entropyTable3 = entropyDiscretizator.discrete(table, 3, 1);
                    DataTable entropyTable5 = entropyDiscretizator.discrete(table, 5, 1);
                    TD4CDiscretizator cosineDiscretizator = new TD4CDiscretizator(table.getInstances(), new CosineDistance());
                    DataTable cosineTable3 = cosineDiscretizator.discrete(table, 3, 1);
                    DataTable cosineTable5 = cosineDiscretizator.discrete(table, 5, 1);
//                    tables[j - 1] = table;
                    writer.dataTableToCsv(new DataTableToCsvRequest(klTable3, csvPath + experimentName + "\\" + "Regular\\" + experimentName + "-" + j + "-regular-kl-3" + csvName, CsvNumberRepresentation.INTEGER_REPRESENTATION, TRAIN_TEST, 100, 0, 1000000));
                    writer.dataTableToCsv(new DataTableToCsvRequest(klTable5, csvPath + experimentName + "\\" + "Regular\\" + experimentName + "-" + j + "-regular-kl-5" + csvName, CsvNumberRepresentation.INTEGER_REPRESENTATION, TRAIN_TEST, 100, 0, 1000000));
                    writer.dataTableToCsv(new DataTableToCsvRequest(entropyTable3, csvPath + experimentName + "\\" + "Regular\\" + experimentName + "-" + j + "-regular-entropy-3" + csvName, CsvNumberRepresentation.INTEGER_REPRESENTATION, TRAIN_TEST, 100, 0, 1000000));
                    writer.dataTableToCsv(new DataTableToCsvRequest(entropyTable5, csvPath + experimentName + "\\" + "Regular\\" + experimentName + "-" + j + "-regular-entropy-5" + csvName, CsvNumberRepresentation.INTEGER_REPRESENTATION, TRAIN_TEST, 100, 0, 1000000));
                    writer.dataTableToCsv(new DataTableToCsvRequest(cosineTable3, csvPath + experimentName + "\\" + "Regular\\" + experimentName + "-" + j + "-regular-cosine-3" + csvName, CsvNumberRepresentation.INTEGER_REPRESENTATION, TRAIN_TEST, 100, 0, 1000000));
                    writer.dataTableToCsv(new DataTableToCsvRequest(cosineTable5, csvPath + experimentName + "\\" + "Regular\\" + experimentName + "-" + j + "-regular-cosine-5" + csvName, CsvNumberRepresentation.INTEGER_REPRESENTATION, TRAIN_TEST, 100, 0, 1000000));
//                    writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + experimentName + "\\" + "Binary\\" + experimentName + "-" + j + "-binary" + csvName, CsvNumberRepresentation.BINARY_REPRESENTATION, TRAIN_TEST, 100));
//                    writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + experimentName + "\\" + "TF\\" + experimentName + "-" + j + "-tf" + csvName, CsvNumberRepresentation.TF_REPRESENTATION, TRAIN_TEST, 100));
//                    writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + experimentName + "\\" + "TF-IDF\\" + experimentName + "-" + j + "-tf-idf" + csvName, CsvNumberRepresentation.TFIDF_REPRESENTATION, TRAIN_TEST, 100));
                }
//                writer.dataTablesToCsv(new DataTablesToCsvRequest(tables, csvPath + experimentName + "\\" + experimentName + "-combined-" + csvName, new CsvNumberRepresentation[] {CsvNumberRepresentation.BINARY_REPRESENTATION ,CsvNumberRepresentation.INTEGER_REPRESENTATION, CsvNumberRepresentation.TF_REPRESENTATION, CsvNumberRepresentation.TFIDF_REPRESENTATION}, TRAIN_TEST, 100));
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
