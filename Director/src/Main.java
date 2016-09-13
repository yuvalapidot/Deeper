import creator.DataTableCreator;
import creator.DumpJsonsToDataTableCreator;
import extractor.CallGramExtractor;
import extractor.IFeatureExtractor;
import model.data.DataTable;
import model.memory.Dump;
import writer.DataTableCsvWriter;
import writer.DataTableToCsvRequest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final String dumpsDirectoryPath = "";
    private static final String csvPath = "";

    public static void main(String[] args) throws IOException, InterruptedException {
        List<IFeatureExtractor<Dump>> extractors = new ArrayList<>();
        extractors.add(new CallGramExtractor(1));
        extractors.add(new CallGramExtractor(2));
        extractors.add(new CallGramExtractor(3));
        extractors.add(new CallGramExtractor(4));
        DataTableCreator creator = new DumpJsonsToDataTableCreator(getJsonFiles(), extractors);
        DataTable table = creator.createDataTable();
        DataTableCsvWriter writer = new DataTableCsvWriter();
        writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath));
    }

    private static List<File> getJsonFiles() throws IOException {
        List<File> files = new ArrayList<>();
        Files.walk(Paths.get(dumpsDirectoryPath)).filter(filePath -> Files.isRegularFile(filePath)).forEach(filePath -> files.add(filePath.toFile()));
        return files;
    }
}
