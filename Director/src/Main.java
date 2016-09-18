import creator.DataTableCreator;
import creator.DumpToDataTableCreator;
import extractor.CallGramExtractor;
import extractor.IFeatureExtractor;
import model.data.DataTable;
import model.instance.DumpInstance;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    private static final int upToN = 5;
    private static final String jsonsDirectoryPath = "D:\\DeepFeaturesExperiment\\Jsons";
    private static final String csvPath = "D:\\DeepFeaturesExperiment\\Results\\";
    private static final String csvName = "-call-gram-data-table.csv";

    public static void main(String[] args) throws IOException, InterruptedException {
        List<IFeatureExtractor<DumpInstance>> extractors = new ArrayList<>();
        List<DumpInstance> dumps = getDumpInstances(getDumps(getJsonFiles()));
        DataTableCsvWriter writer = new DataTableCsvWriter();
        for (int i = 1; i <= upToN; i++) {
            IFeatureExtractor<DumpInstance> extractor = new CallGramExtractor(i);
            DataTableCreator creator = new DumpToDataTableCreator(dumps);
            creator.addExtractor(extractor);
            DataTable table = creator.createDataTable();
            writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + "Regular\\" + i + "-regular" + csvName, CsvNumberRepresentation.INTEGER_REPRESENTATION));
            writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + "Binary\\" + i + "-binary" + csvName, CsvNumberRepresentation.BINARY_REPRESENTATION));
            writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + "TF\\" + i + "-tf" + csvName, CsvNumberRepresentation.TF_REPRESENTATION));
            writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + "TF-IDF\\" + i + "-tf-idf" + csvName, CsvNumberRepresentation.TFIDF_REPRESENTATION));
            extractors.add(extractor);
        }
        DataTableCreator creator = new DumpToDataTableCreator(dumps, extractors);
        DataTable table = creator.createDataTable();
        writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + "Regular\\combined-1-regular-" + upToN + csvName, CsvNumberRepresentation.INTEGER_REPRESENTATION));
        writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + "Binary\\combined-1-binary-" + upToN + csvName, CsvNumberRepresentation.BINARY_REPRESENTATION));
        writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + "TF\\combined-1-tf-" + upToN + csvName, CsvNumberRepresentation.TF_REPRESENTATION));
        writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + "TF-IDF\\combined-1-tf-idf-" + upToN + csvName, CsvNumberRepresentation.TFIDF_REPRESENTATION));
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
        for (Dump dump : dumps) {
            instances.add(new DumpInstance(dump));
        }
        return instances;
    }
}
