import creator.DataTableCreator;
import creator.DumpToDataTableCreator;
import extractor.CallGramExtractor;
import extractor.IFeatureExtractor;
import model.data.DataTable;
import model.memory.Dump;
import reader.JsonDumpReader;
import reader.JsonToDumpRequest;
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
        List<IFeatureExtractor<Dump>> extractors = new ArrayList<>();
        List<Dump> dumps = getDumps(getJsonFiles());
        for (int i = 1; i <= upToN; i++) {
            IFeatureExtractor<Dump> extractor = new CallGramExtractor(i);
            DataTableCreator creator = new DumpToDataTableCreator(dumps);
            creator.addExtractor(extractor);
            DataTable table = creator.createDataTable();
            DataTableCsvWriter writer = new DataTableCsvWriter();
            writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + i + csvName));
            extractors.add(extractor);
        }
        DataTableCreator creator = new DumpToDataTableCreator(dumps, extractors);
        DataTable table = creator.createDataTable();
        DataTableCsvWriter writer = new DataTableCsvWriter();
        writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + "combined-1-" + upToN + csvName));
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
}
