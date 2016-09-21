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
    private static final String jsonsDirectoryPath = "D:\\DeepFeaturesExperiment\\Jsons";
    private static final String csvPath = "D:\\DeepFeaturesExperiment\\Results\\";
    private static final String csvName = "-call-gram-data-table.csv";

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
        for (Dump dump : dumps) {
            if (dump.getName().contains("Empty")) {
                // DS1
//                dump.setClassification("DS1");
                instances.add(new DumpInstance(dump, (random.nextDouble() > 0.7) ? InstanceSetType.TRAIN_SET : InstanceSetType.TEST_SET));
            } else if (dump.getName().contains("ProcMon")) {
                // DS3
//                dump.setClassification("DS3");
                instances.add(new DumpInstance(dump, (random.nextDouble() > 0.7) ? InstanceSetType.TRAIN_SET : InstanceSetType.TEST_SET));
            } else if (dump.getName().contains("Cerber")) {
                // DS4
//                dump.setClassification("DS4");
                instances.add(new DumpInstance(dump, InstanceSetType.TEST_SET));
            } else if (dump.getName().contains("HiddenTear")) {
                // DS2
//                dump.setClassification("DS2");
                instances.add(new DumpInstance(dump, (random.nextDouble() > 0.7) ? InstanceSetType.TRAIN_SET : InstanceSetType.TEST_SET));
            }
        }
        return instances;
    }
}
