import creator.DataTableCreator;
import creator.DumpToDataTableCreator;
import extractor.CallGramExtractor;
import extractor.IFeatureExtractor;
import model.data.DataTable;
import model.instance.DumpInstance;
import model.instance.InstanceSetType;
import model.memory.Dump;
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

    private static final int upToN = 5;
    private static final String jsonsDirectoryPath = "C:\\Users\\yuval\\Documents\\Thesis\\Feature Extraction\\Jsons";
    private static final String csvPath = "C:\\Users\\yuval\\Documents\\Thesis\\Feature Extraction\\Results\\";
    private static final String csvName = "-call-gram-data-table.csv";

    private static final int DS1_COUNT = 100;
    private static final int DS2_COUNT = 446;
    private static final int DS3_COUNT = 100;
    private static final int DS4_COUNT = 93;

    private static final Set<InstanceSetType> TRAIN = new HashSet<InstanceSetType>(Arrays.asList(InstanceSetType.TRAIN_SET));
    private static final Set<InstanceSetType> TEST = new HashSet<InstanceSetType>(Arrays.asList(InstanceSetType.TEST_SET));
    private static final Set<InstanceSetType> TRAIN_TEST = new HashSet<InstanceSetType>(Arrays.asList(InstanceSetType.TRAIN_SET, InstanceSetType.TEST_SET));

    public static void main(String[] args) throws IOException, InterruptedException {
        List<IFeatureExtractor<DumpInstance>> extractors = new ArrayList<>();
        List<DumpInstance> dumps = getDumpInstances(getDumps(getJsonFiles()));
        DataTableCsvWriter writer = new DataTableCsvWriter();

        for (int i = 1; i <= upToN; i++) {
            IFeatureExtractor<DumpInstance> extractor = new CallGramExtractor(i);
            DataTableCreator creator = new DumpToDataTableCreator(dumps);
            creator.addExtractor(extractor);
            DataTable table = creator.createDataTable();
//            writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + "Regular\\" + i + "-regular-train" + csvName, CsvNumberRepresentation.INTEGER_REPRESENTATION, TRAIN));
//            writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + "Regular\\" + i + "-regular-test" + csvName, CsvNumberRepresentation.INTEGER_REPRESENTATION, TEST));
            writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + "Regular\\" + i + "-regular-train+test" + csvName, CsvNumberRepresentation.INTEGER_REPRESENTATION, TRAIN_TEST));
//            writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + "Binary\\" + i + "-binary-train" + csvName, CsvNumberRepresentation.BINARY_REPRESENTATION, TRAIN));
//            writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + "Binary\\" + i + "-binary-test" + csvName, CsvNumberRepresentation.BINARY_REPRESENTATION, TEST));
            writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + "Binary\\" + i + "-binary-train+test" + csvName, CsvNumberRepresentation.BINARY_REPRESENTATION, TRAIN_TEST));
//            writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + "TF\\" + i + "-tf-train" + csvName, CsvNumberRepresentation.TF_REPRESENTATION, TRAIN));
//            writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + "TF\\" + i + "-tf-test" + csvName, CsvNumberRepresentation.TF_REPRESENTATION, TEST));
            writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + "TF\\" + i + "-tf-train+test" + csvName, CsvNumberRepresentation.TF_REPRESENTATION, TRAIN_TEST));
//            writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + "TF-IDF\\" + i + "-tf-idf-train" + csvName, CsvNumberRepresentation.TFIDF_REPRESENTATION, TRAIN));
//            writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + "TF-IDF\\" + i + "-tf-idf-test" + csvName, CsvNumberRepresentation.TFIDF_REPRESENTATION, TEST));
            writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + "TF-IDF\\" + i + "-tf-idf-train+test" + csvName, CsvNumberRepresentation.TFIDF_REPRESENTATION, TRAIN_TEST));
            extractors.add(extractor);
        }
        DataTableCreator creator = new DumpToDataTableCreator(dumps, extractors);
        DataTable table = creator.createDataTable();
//        writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + "Regular\\combined-regular-train-1-" + upToN + csvName, CsvNumberRepresentation.INTEGER_REPRESENTATION, TRAIN));
//        writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + "Regular\\combined-regular-test-1-" + upToN + csvName, CsvNumberRepresentation.INTEGER_REPRESENTATION, TEST));
        writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + "Regular\\combined-regular-train+test-1-" + upToN + csvName, CsvNumberRepresentation.INTEGER_REPRESENTATION, TRAIN_TEST));
//        writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + "Binary\\combined-binary-train-1-" + upToN + csvName, CsvNumberRepresentation.BINARY_REPRESENTATION, TRAIN));
//        writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + "Binary\\combined-binary-test-1-" + upToN + csvName, CsvNumberRepresentation.BINARY_REPRESENTATION, TEST));
        writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + "Binary\\combined-binary-train+test-1-" + upToN + csvName, CsvNumberRepresentation.BINARY_REPRESENTATION, TRAIN_TEST));
//        writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + "TF\\combined-tf-train-1-" + upToN + csvName, CsvNumberRepresentation.TF_REPRESENTATION, TRAIN));
//        writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + "TF\\combined-tf-test-1-" + upToN + csvName, CsvNumberRepresentation.TF_REPRESENTATION, TEST));
        writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + "TF\\combined-tf-train+test-1-" + upToN + csvName, CsvNumberRepresentation.TF_REPRESENTATION, TRAIN_TEST));
//        writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + "TF-IDF\\combined-tf-idf-train-1-" + upToN + csvName, CsvNumberRepresentation.TFIDF_REPRESENTATION, TRAIN));
//        writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + "TF-IDF\\combined-tf-idf-test-1-" + upToN + csvName, CsvNumberRepresentation.TFIDF_REPRESENTATION, TEST));
        writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + "TF-IDF\\combined-tf-idf-train+test-1-" + upToN + csvName, CsvNumberRepresentation.TFIDF_REPRESENTATION, TRAIN_TEST));
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

    private static List<DumpInstance> getDumpInstances(List<Dump> dumps) {
        List<DumpInstance> instances = new ArrayList<>();
        Random random = new Random(0);
        int ds1Count = 0, ds2Count = 0, ds3Count = 0, ds4Count = 0;
        for (Dump dump : dumps) {
            if (dump.getName().contains("Empty")) {
                // DS1
                ds1Count++;
                continue;
//                dump.setClassification("DS1");
//                instances.add(new DumpInstance(dump, InstanceSetType.TRAIN_SET));
            } else if (dump.getName().contains("ProcMon")) {
                // DS3
                ds3Count++;
                dump.setClassification("BENIGN");
                instances.add(new DumpInstance(dump, (ds3Count <= DS3_COUNT / 2) ? InstanceSetType.TRAIN_SET : InstanceSetType.TEST_SET));
            } else if (dump.getName().contains("Cerber")) {
                // DS4
                ds4Count++;
                dump.setClassification("MALICIOUS");
                instances.add(new DumpInstance(dump, (ds2Count <= DS2_COUNT / 2) ? InstanceSetType.TRAIN_SET : InstanceSetType.TEST_SET));
            } else if (dump.getName().contains("HiddenTear")) {
                // DS2
                ds2Count++;
                dump.setClassification("MALICIOUS");
                instances.add(new DumpInstance(dump, InstanceSetType.TEST_SET));
            }
        }
        return instances;
    }
}
