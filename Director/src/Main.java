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
import writer.CsvNumberRepresentation;
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

    private static final int upToN = 4;
    private static final String jsonsDirectoryPath = "C:\\Users\\yuval\\Dropbox\\University\\Thesis\\Results\\Feature Extraction\\Deep Features\\Jsons";
    private static final String csvPath = "C:\\Users\\yuval\\Dropbox\\University\\Thesis\\Results\\Feature Extraction\\Deep Features\\Results\\Experiment";
    private static final String csvName = "-call-gram-data-table.csv";

    private static final int DS1_COUNT = 100;
    private static final int DS2_COUNT = 100;
    private static final int DS3_COUNT = 100;
    private static final int DS4_COUNT = 93;
    private static final int DS5_COUNT = 100;
    private static final int DS6_COUNT = 97;

    private static final Set<InstanceSetType> TRAIN = new HashSet<InstanceSetType>(Arrays.asList(InstanceSetType.TRAIN_SET));
    private static final Set<InstanceSetType> TEST = new HashSet<InstanceSetType>(Arrays.asList(InstanceSetType.TEST_SET));
    private static final Set<InstanceSetType> TRAIN_TEST = new HashSet<InstanceSetType>(Arrays.asList(InstanceSetType.TRAIN_SET, InstanceSetType.TEST_SET));

    public static void main(String[] args) throws IOException, InterruptedException {
        DataTableCsvWriter writer = new DataTableCsvWriter();
        List<Dump> dumps = getDumps(getJsonFiles());
        for (int i = 4; i <= 7; i++) {
            List<IFeatureExtractor<DumpInstance>> extractors = new ArrayList<>();
            List<DumpInstance> dumpInstances = getDumpInstances(i, dumps);
            int trainCounter = 0;
            for (DumpInstance instance : dumpInstances) {
                if (instance.getSetType().equals(InstanceSetType.TRAIN_SET)) {
                    trainCounter++;
                }
            }
            log.info("Experiment " + i + " train percentage: " + (((double) trainCounter) / dumpInstances.size()) * 100);
            for (int j = 1; j <= upToN; j++) {
                IFeatureExtractor<DumpInstance> extractor = new CallGramExtractor(j);
                DataTableCreator creator = new DumpToDataTableCreator(dumpInstances);
                creator.addExtractor(extractor);
                DataTable table = creator.createDataTable();
//            writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + i + "\\" + "Regular\\" + j + "-regular-train" + csvName, CsvNumberRepresentation.INTEGER_REPRESENTATION, TRAIN));
//            writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + i + "\\" + "Regular\\" + j + "-regular-test" + csvName, CsvNumberRepresentation.INTEGER_REPRESENTATION, TEST));
                writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + i + "\\" + "Regular\\" + j + "-regular-train+test" + csvName, CsvNumberRepresentation.INTEGER_REPRESENTATION, TRAIN_TEST));
//            writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + i + "\\" + "Binary\\" + j + "-binary-train" + csvName, CsvNumberRepresentation.BINARY_REPRESENTATION, TRAIN));
//            writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + i + "\\" + "Binary\\" + j + "-binary-test" + csvName, CsvNumberRepresentation.BINARY_REPRESENTATION, TEST));
                writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + i + "\\" + "Binary\\" + j + "-binary-train+test" + csvName, CsvNumberRepresentation.BINARY_REPRESENTATION, TRAIN_TEST));
//            writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + i + "\\" + "TF\\" + j + "-tf-train" + csvName, CsvNumberRepresentation.TF_REPRESENTATION, TRAIN));
//            writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + i + "\\" + "TF\\" + j + "-tf-test" + csvName, CsvNumberRepresentation.TF_REPRESENTATION, TEST));
                writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + i + "\\" + "TF\\" + j + "-tf-train+test" + csvName, CsvNumberRepresentation.TF_REPRESENTATION, TRAIN_TEST));
//            writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + i + "\\" + "TF-IDF\\" + j + "-tf-idf-train" + csvName, CsvNumberRepresentation.TFIDF_REPRESENTATION, TRAIN));
//            writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + i + "\\" + "TF-IDF\\" + j + "-tf-idf-test" + csvName, CsvNumberRepresentation.TFIDF_REPRESENTATION, TEST));
                writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + i + "\\" + "TF-IDF\\" + j + "-tf-idf-train+test" + csvName, CsvNumberRepresentation.TFIDF_REPRESENTATION, TRAIN_TEST));
                extractors.add(extractor);
            }
            DataTableCreator creator = new DumpToDataTableCreator(dumpInstances, extractors);
            DataTable table = creator.createDataTable();
//        writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + i + "\\" + "Regular\\combined-regular-train-1-" + upToN + csvName, CsvNumberRepresentation.INTEGER_REPRESENTATION, TRAIN));
//        writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + i + "\\" + "Regular\\combined-regular-test-1-" + upToN + csvName, CsvNumberRepresentation.INTEGER_REPRESENTATION, TEST));
            writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + i + "\\" + "Regular\\combined-regular-train+test-1-" + upToN + csvName, CsvNumberRepresentation.INTEGER_REPRESENTATION, TRAIN_TEST));
//        writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + i + "\\" + "Binary\\combined-binary-train-1-" + upToN + csvName, CsvNumberRepresentation.BINARY_REPRESENTATION, TRAIN));
//        writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + i + "\\" + "Binary\\combined-binary-test-1-" + upToN + csvName, CsvNumberRepresentation.BINARY_REPRESENTATION, TEST));
            writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + i + "\\" + "Binary\\combined-binary-train+test-1-" + upToN + csvName, CsvNumberRepresentation.BINARY_REPRESENTATION, TRAIN_TEST));
//        writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + i + "\\" + "TF\\combined-tf-train-1-" + upToN + csvName, CsvNumberRepresentation.TF_REPRESENTATION, TRAIN));
//        writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + i + "\\" + "TF\\combined-tf-test-1-" + upToN + csvName, CsvNumberRepresentation.TF_REPRESENTATION, TEST));
            writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + i + "\\" + "TF\\combined-tf-train+test-1-" + upToN + csvName, CsvNumberRepresentation.TF_REPRESENTATION, TRAIN_TEST));
//        writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + i + "\\" + "TF-IDF\\combined-tf-idf-train-1-" + upToN + csvName, CsvNumberRepresentation.TFIDF_REPRESENTATION, TRAIN));
//        writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + i + "\\" + "TF-IDF\\combined-tf-idf-test-1-" + upToN + csvName, CsvNumberRepresentation.TFIDF_REPRESENTATION, TEST));
            writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + i + "\\" + "TF-IDF\\combined-tf-idf-train+test-1-" + upToN + csvName, CsvNumberRepresentation.TFIDF_REPRESENTATION, TRAIN_TEST));
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

    private static List<DumpInstance> getDumpInstances(int experimentNumber, List<Dump> dumps) {
        switch (experimentNumber) {
            case 0: return getDumpInstancesExperiment0(dumps);
            case 1: return getDumpInstancesExperiment1(dumps);
            case 2: return getDumpInstancesExperiment2(dumps);
            case 3: return getDumpInstancesExperiment3(dumps);
            case 4: return getDumpInstancesExperiment4(dumps);
            case 5: return getDumpInstancesExperiment5(dumps);
            case 6: return getDumpInstancesExperiment6(dumps);
            case 7: return getDumpInstancesExperiment7(dumps);
        }
        return new ArrayList<>();
    }

    private static List<DumpInstance> getDumpInstancesExperiment0(List<Dump> dumps) {
        List<DumpInstance> instances = new ArrayList<>();
        int ds1Count = 0, ds2Count = 0, ds3Count = 0, ds4Count = 0, ds5Count = 0, ds6Count = 0;
        for (Dump dump : dumps) {
            if (dump.getName().contains("Empty")) {
                // DS1
                ds1Count++;
                dump.setClassification("BENIGN");
                instances.add(new DumpInstance(dump, (ds1Count <= DS1_COUNT / 1.5) ? InstanceSetType.TRAIN_SET : InstanceSetType.TEST_SET));
            } else if (dump.getName().contains("ProcMon")) {
                // DS3
                ds3Count++;
                dump.setClassification("BENIGN");
                instances.add(new DumpInstance(dump, (ds3Count <= DS3_COUNT / 1.5) ? InstanceSetType.TRAIN_SET : InstanceSetType.TEST_SET));
            } else if (dump.getName().contains("Cerber")) {
                // DS4
                ds4Count++;
                dump.setClassification("MALICIOUS");
                instances.add(new DumpInstance(dump, InstanceSetType.TEST_SET));
            } else if (dump.getName().contains("HiddenTear")) {
                // DS2
                ds2Count++;
                dump.setClassification("MALICIOUS");
                instances.add(new DumpInstance(dump, InstanceSetType.TEST_SET));
            } else if (dump.getName().contains("CryptoLocker3")) {
                // DS5
                ds5Count++;
                dump.setClassification("MALICIOUS");
                instances.add(new DumpInstance(dump, InstanceSetType.TEST_SET));
            } else if (dump.getName().contains("Vipasana")) {
                // DS6
                ds6Count++;
                dump.setClassification("MALICIOUS");
                instances.add(new DumpInstance(dump, InstanceSetType.TEST_SET));
            }
        }
        return instances;
    }

    private static List<DumpInstance> getDumpInstancesExperiment1(List<Dump> dumps) {
        List<DumpInstance> instances = new ArrayList<>();
        int ds1Count = 0, ds2Count = 0, ds3Count = 0, ds4Count = 0, ds5Count = 0, ds6Count = 0;
        for (Dump dump : dumps) {
            if (dump.getName().contains("Empty")) {
                // DS1
                ds1Count++;
                dump.setClassification("BENIGN");
                instances.add(new DumpInstance(dump, InstanceSetType.TRAIN_SET));
            } else if (dump.getName().contains("ProcMon")) {
                // DS3
                ds3Count++;
                dump.setClassification("ANOMALY");
                instances.add(new DumpInstance(dump, InstanceSetType.TRAIN_SET));
            } else if (dump.getName().contains("Cerber")) {
                // DS4
                ds4Count++;
                dump.setClassification("ANOMALY");
                instances.add(new DumpInstance(dump, InstanceSetType.TRAIN_SET));
            } else if (dump.getName().contains("HiddenTear")) {
                // DS2
                ds2Count++;
                dump.setClassification("ANOMALY");
                instances.add(new DumpInstance(dump, InstanceSetType.TRAIN_SET));
            } else if (dump.getName().contains("CryptoLocker3")) {
                // DS5
                ds5Count++;
                dump.setClassification("ANOMALY");
                instances.add(new DumpInstance(dump, InstanceSetType.TRAIN_SET));
            } else if (dump.getName().contains("Vipasana")) {
                // DS6
                ds6Count++;
                dump.setClassification("ANOMALY");
                instances.add(new DumpInstance(dump, InstanceSetType.TRAIN_SET));
            }
        }
        return instances;
    }

    private static List<DumpInstance> getDumpInstancesExperiment2(List<Dump> dumps) {
        List<DumpInstance> instances = new ArrayList<>();
        int ds1Count = 0, ds2Count = 0, ds3Count = 0, ds4Count = 0, ds5Count = 0, ds6Count = 0;
        for (Dump dump : dumps) {
            if (dump.getName().contains("Empty")) {
                // DS1
                ds1Count++;
                dump.setClassification("BENIGN");
                instances.add(new DumpInstance(dump, InstanceSetType.TRAIN_SET));
            } else if (dump.getName().contains("ProcMon")) {
                // DS3
                ds3Count++;
                dump.setClassification("BENIGN");
                instances.add(new DumpInstance(dump, InstanceSetType.TRAIN_SET));
            } else if (dump.getName().contains("Cerber")) {
                // DS4
                ds4Count++;
                dump.setClassification("MALICIOUS");
                instances.add(new DumpInstance(dump, InstanceSetType.TRAIN_SET));
            } else if (dump.getName().contains("HiddenTear")) {
                // DS2
                ds2Count++;
                dump.setClassification("MALICIOUS");
                instances.add(new DumpInstance(dump, InstanceSetType.TRAIN_SET));
            } else if (dump.getName().contains("CryptoLocker3")) {
                // DS5
                ds5Count++;
                dump.setClassification("MALICIOUS");
                instances.add(new DumpInstance(dump, InstanceSetType.TRAIN_SET));
            } else if (dump.getName().contains("Vipasana")) {
                // DS6
                ds6Count++;
                dump.setClassification("MALICIOUS");
                instances.add(new DumpInstance(dump, InstanceSetType.TRAIN_SET));
            }
        }
        return instances;
    }

    private static List<DumpInstance> getDumpInstancesExperiment3(List<Dump> dumps) {
        List<DumpInstance> instances = new ArrayList<>();
        int ds1Count = 0, ds2Count = 0, ds3Count = 0, ds4Count = 0, ds5Count = 0, ds6Count = 0;
        for (Dump dump : dumps) {
            if (dump.getName().contains("Empty")) {
                // DS1
                ds1Count++;
                dump.setClassification("DS1");
                instances.add(new DumpInstance(dump, InstanceSetType.TRAIN_SET));
            } else if (dump.getName().contains("ProcMon")) {
                // DS3
                ds3Count++;
                dump.setClassification("DS3");
                instances.add(new DumpInstance(dump, InstanceSetType.TRAIN_SET));
            } else if (dump.getName().contains("Cerber")) {
                // DS4
                ds4Count++;
                dump.setClassification("DS4");
                instances.add(new DumpInstance(dump, InstanceSetType.TRAIN_SET));
            } else if (dump.getName().contains("HiddenTear")) {
                // DS2
                ds2Count++;
                dump.setClassification("DS2");
                instances.add(new DumpInstance(dump, InstanceSetType.TRAIN_SET));
            } else if (dump.getName().contains("CryptoLocker3")) {
                // DS5
                ds5Count++;
                dump.setClassification("DS5");
                instances.add(new DumpInstance(dump, InstanceSetType.TRAIN_SET));
            } else if (dump.getName().contains("Vipasana")) {
                // DS6
                ds6Count++;
                dump.setClassification("DS6");
                instances.add(new DumpInstance(dump, InstanceSetType.TRAIN_SET));
            }
        }
        return instances;
    }

    private static List<DumpInstance> getDumpInstancesExperiment4(List<Dump> dumps) {
        List<DumpInstance> instances = new ArrayList<>();
        int ds1Count = 0, ds2Count = 0, ds3Count = 0, ds4Count = 0, ds5Count = 0, ds6Count = 0;
        for (Dump dump : dumps) {
            if (dump.getName().contains("Empty")) {
                // DS1
                ds1Count++;
                dump.setClassification("BENIGN");
                instances.add(new DumpInstance(dump, (ds1Count <= DS1_COUNT / 1.5) ? InstanceSetType.TRAIN_SET : InstanceSetType.TEST_SET));
            } else if (dump.getName().contains("ProcMon")) {
                // DS3
                ds3Count++;
                dump.setClassification("BENIGN");
                instances.add(new DumpInstance(dump, (ds3Count <= DS3_COUNT / 1.5) ? InstanceSetType.TRAIN_SET : InstanceSetType.TEST_SET));
            } else if (dump.getName().contains("Cerber")) {
                // DS4
                ds4Count++;
                dump.setClassification("MALICIOUS");
                instances.add(new DumpInstance(dump, (ds4Count <= DS4_COUNT / 1.5) ? InstanceSetType.TRAIN_SET : InstanceSetType.TEST_SET));
            } else if (dump.getName().contains("HiddenTear")) {
                // DS2
                ds2Count++;
                dump.setClassification("MALICIOUS");
                instances.add(new DumpInstance(dump, InstanceSetType.TEST_SET));
            } else if (dump.getName().contains("CryptoLocker3")) {
                // DS5
                ds5Count++;
                dump.setClassification("MALICIOUS");
                instances.add(new DumpInstance(dump, (ds5Count <= DS5_COUNT / 1.5) ? InstanceSetType.TRAIN_SET : InstanceSetType.TEST_SET));
            } else if (dump.getName().contains("Vipasana")) {
                // DS6
                ds6Count++;
                dump.setClassification("MALICIOUS");
                instances.add(new DumpInstance(dump, (ds6Count <= DS6_COUNT / 1.5) ? InstanceSetType.TRAIN_SET : InstanceSetType.TEST_SET));
            }
        }
        return instances;
    }

    private static List<DumpInstance> getDumpInstancesExperiment5(List<Dump> dumps) {
        List<DumpInstance> instances = new ArrayList<>();
        int ds1Count = 0, ds2Count = 0, ds3Count = 0, ds4Count = 0, ds5Count = 0, ds6Count = 0;
        for (Dump dump : dumps) {
            if (dump.getName().contains("Empty")) {
                // DS1
                ds1Count++;
                dump.setClassification("BENIGN");
                instances.add(new DumpInstance(dump, (ds1Count <= DS1_COUNT / 1.5) ? InstanceSetType.TRAIN_SET : InstanceSetType.TEST_SET));
            } else if (dump.getName().contains("ProcMon")) {
                // DS3
                ds3Count++;
                dump.setClassification("BENIGN");
                instances.add(new DumpInstance(dump, (ds3Count <= DS3_COUNT / 1.5) ? InstanceSetType.TRAIN_SET : InstanceSetType.TEST_SET));
            } else if (dump.getName().contains("Cerber")) {
                // DS4
                ds4Count++;
                dump.setClassification("MALICIOUS");
                instances.add(new DumpInstance(dump, InstanceSetType.TEST_SET));
            } else if (dump.getName().contains("HiddenTear")) {
                // DS2
                ds2Count++;
                dump.setClassification("MALICIOUS");
                instances.add(new DumpInstance(dump, (ds2Count <= DS2_COUNT / 1.5) ? InstanceSetType.TRAIN_SET : InstanceSetType.TEST_SET));
            } else if (dump.getName().contains("CryptoLocker3")) {
                // DS5
                ds5Count++;
                dump.setClassification("MALICIOUS");
                instances.add(new DumpInstance(dump, (ds5Count <= DS5_COUNT / 1.5) ? InstanceSetType.TRAIN_SET : InstanceSetType.TEST_SET));
            } else if (dump.getName().contains("Vipasana")) {
                // DS6
                ds6Count++;
                dump.setClassification("MALICIOUS");
                instances.add(new DumpInstance(dump, (ds6Count <= DS6_COUNT / 1.5) ? InstanceSetType.TRAIN_SET : InstanceSetType.TEST_SET));
            }
        }
        return instances;
    }

    private static List<DumpInstance> getDumpInstancesExperiment6(List<Dump> dumps) {
        List<DumpInstance> instances = new ArrayList<>();
        int ds1Count = 0, ds2Count = 0, ds3Count = 0, ds4Count = 0, ds5Count = 0, ds6Count = 0;
        for (Dump dump : dumps) {
            if (dump.getName().contains("Empty")) {
                // DS1
                ds1Count++;
                dump.setClassification("BENIGN");
                instances.add(new DumpInstance(dump, (ds1Count <= DS1_COUNT / 1.5) ? InstanceSetType.TRAIN_SET : InstanceSetType.TEST_SET));
            } else if (dump.getName().contains("ProcMon")) {
                // DS3
                ds3Count++;
                dump.setClassification("BENIGN");
                instances.add(new DumpInstance(dump, (ds3Count <= DS3_COUNT / 1.5) ? InstanceSetType.TRAIN_SET : InstanceSetType.TEST_SET));
            } else if (dump.getName().contains("Cerber")) {
                // DS4
                ds4Count++;
                dump.setClassification("MALICIOUS");
                instances.add(new DumpInstance(dump, (ds4Count <= DS4_COUNT / 1.5) ? InstanceSetType.TRAIN_SET : InstanceSetType.TEST_SET));
            } else if (dump.getName().contains("HiddenTear")) {
                // DS2
                ds2Count++;
                dump.setClassification("MALICIOUS");
                instances.add(new DumpInstance(dump, (ds2Count <= DS2_COUNT / 1.5) ? InstanceSetType.TRAIN_SET : InstanceSetType.TEST_SET));
            } else if (dump.getName().contains("CryptoLocker3")) {
                // DS5
                ds5Count++;
                dump.setClassification("MALICIOUS");
                instances.add(new DumpInstance(dump, InstanceSetType.TEST_SET));
            } else if (dump.getName().contains("Vipasana")) {
                // DS6
                ds6Count++;
                dump.setClassification("MALICIOUS");
                instances.add(new DumpInstance(dump, (ds6Count <= DS6_COUNT / 1.5) ? InstanceSetType.TRAIN_SET : InstanceSetType.TEST_SET));
            }
        }
        return instances;
    }

    private static List<DumpInstance> getDumpInstancesExperiment7(List<Dump> dumps) {
        List<DumpInstance> instances = new ArrayList<>();
        int ds1Count = 0, ds2Count = 0, ds3Count = 0, ds4Count = 0, ds5Count = 0, ds6Count = 0;
        for (Dump dump : dumps) {
            if (dump.getName().contains("Empty")) {
                // DS1
                ds1Count++;
                dump.setClassification("BENIGN");
                instances.add(new DumpInstance(dump, (ds1Count <= DS1_COUNT / 1.5) ? InstanceSetType.TRAIN_SET : InstanceSetType.TEST_SET));
            } else if (dump.getName().contains("ProcMon")) {
                // DS3
                ds3Count++;
                dump.setClassification("BENIGN");
                instances.add(new DumpInstance(dump, (ds3Count <= DS3_COUNT / 1.5) ? InstanceSetType.TRAIN_SET : InstanceSetType.TEST_SET));
            } else if (dump.getName().contains("Cerber")) {
                // DS4
                ds4Count++;
                dump.setClassification("MALICIOUS");
                instances.add(new DumpInstance(dump, (ds4Count <= DS4_COUNT / 1.5) ? InstanceSetType.TRAIN_SET : InstanceSetType.TEST_SET));
            } else if (dump.getName().contains("HiddenTear")) {
                // DS2
                ds2Count++;
                dump.setClassification("MALICIOUS");
                instances.add(new DumpInstance(dump, (ds2Count <= DS2_COUNT / 1.5) ? InstanceSetType.TRAIN_SET : InstanceSetType.TEST_SET));
            } else if (dump.getName().contains("CryptoLocker3")) {
                // DS5
                ds5Count++;
                dump.setClassification("MALICIOUS");
                instances.add(new DumpInstance(dump, (ds5Count <= DS5_COUNT / 1.5) ? InstanceSetType.TRAIN_SET : InstanceSetType.TEST_SET));
            } else if (dump.getName().contains("Vipasana")) {
                // DS6
                ds6Count++;
                dump.setClassification("MALICIOUS");
                instances.add(new DumpInstance(dump, InstanceSetType.TEST_SET));
            }
        }
        return instances;
    }
}
