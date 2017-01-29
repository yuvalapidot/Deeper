import creator.DataTableCreator;
import creator.DumpToDataTableCreator;
import extractor.CallGramExtractor;
import extractor.IFeatureExtractor;
import model.DataSet;
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

public class TimeSeriesMain {

    private static final Logger log = LogManager.getLogger(TimeSeriesMain.class);

    private static final int upToN = 5;
    private static final String jsonsDirectoryPath = "C:\\Users\\yuval\\Dropbox\\NGrams\\Jsons";
    private static final String csvPath = "C:\\Users\\yuval\\Dropbox\\NGrams\\Results Time Series\\Experiment";
    private static final String csvName = "-gram.csv";

    private static final DataSet DSB1 = new DataSet("Baseline", "Baseline", 100);
    private static final DataSet DSB2 = new DataSet("Procmon", "Procmon", 100);
    private static final DataSet DSB3 = new DataSet("Avast", "Avast", 100);
    private static final DataSet DSB4 = new DataSet("Wireshark", "Wireshark", 100);
    private static final DataSet DSB5 = new DataSet("Defrag", "Defrag", 100);
    private static final DataSet DSM1 = new DataSet("HiddenTear", "HiddenTear", 100);
    private static final DataSet DSM2 = new DataSet("Cerber", "Cerber", 100);
    private static final DataSet DSM3 = new DataSet("TeslaCrypt", "TeslaCrypt", 100);
    private static final DataSet DSM4 = new DataSet("Vipasana", "Vipasana", 100);
    private static final DataSet DSM5 = new DataSet("Chimera", "Chimera", 100);

    private static final int TRAIN_TEST_SPLIT_PERCENTAGE = 80;

    private static final DumpInstanceCreator[][] creators = {
            {
                    new DumpInstanceCreator(DSB1.getName(), DSB1.getSymbol(), DSB1.getCount(), 100),
                    new DumpInstanceCreator(DSB2.getName(), DSB2.getSymbol(), DSB2.getCount(), 100),
            },
            {
                    new DumpInstanceCreator(DSB1.getName(), DSB1.getSymbol(), DSB1.getCount(), 100),
                    new DumpInstanceCreator(DSB3.getName(), DSB3.getSymbol(), DSB3.getCount(), 100),
            },
            {
                    new DumpInstanceCreator(DSB1.getName(), DSB1.getSymbol(), DSB1.getCount(), 100),
                    new DumpInstanceCreator(DSB4.getName(), DSB4.getSymbol(), DSB4.getCount(), 100),
            },
            {
                    new DumpInstanceCreator(DSB1.getName(), DSB1.getSymbol(), DSB1.getCount(), 100),
                    new DumpInstanceCreator(DSB5.getName(), DSB5.getSymbol(), DSB5.getCount(), 100),
            },
            {
                    new DumpInstanceCreator(DSB1.getName(), DSB1.getSymbol(), DSB1.getCount(), 100),
                    new DumpInstanceCreator(DSM1.getName(), DSM1.getSymbol(), DSM1.getCount(), 100),
            },
            {
                    new DumpInstanceCreator(DSB1.getName(), DSB1.getSymbol(), DSB1.getCount(), 100),
                    new DumpInstanceCreator(DSM2.getName(), DSM2.getSymbol(), DSM2.getCount(), 100),
            },
            {
                    new DumpInstanceCreator(DSB1.getName(), DSB1.getSymbol(), DSB1.getCount(), 100),
                    new DumpInstanceCreator(DSM3.getName(), DSM3.getSymbol(), DSM3.getCount(), 100),
            },
            {
                    new DumpInstanceCreator(DSB1.getName(), DSB1.getSymbol(), DSB1.getCount(), 100),
                    new DumpInstanceCreator(DSM4.getName(), DSM4.getSymbol(), DSM4.getCount(), 100),
            },
            {
                    new DumpInstanceCreator(DSB1.getName(), DSB1.getSymbol(), DSB1.getCount(), 100),
                    new DumpInstanceCreator(DSM5.getName(), DSM5.getSymbol(), DSM5.getCount(), 100),
            },
    };

    private static final Set<InstanceSetType> TRAIN = new HashSet<>(Collections.singletonList(InstanceSetType.TRAIN_SET));
    private static final Set<InstanceSetType> TEST = new HashSet<>(Collections.singletonList(InstanceSetType.TEST_SET));
    private static final Set<InstanceSetType> TRAIN_TEST = new HashSet<>(Arrays.asList(InstanceSetType.TRAIN_SET, InstanceSetType.TEST_SET));

    public static void main(String[] args) throws IOException, InterruptedException {
        DataTableCsvWriter writer = new DataTableCsvWriter();
        List<Dump> dumps = getDumps(getJsonFiles());
        for (int i = 0; i < creators.length; i++) {
            List<IFeatureExtractor<DumpInstance>> extractors = new ArrayList<>();
            List<DumpInstance> dumpInstances = getDumpInstances(dumps, creators[i]);
            for (int j = 1; j <= upToN; j++) {
                IFeatureExtractor<DumpInstance> extractor = new CallGramExtractor(j);
                DataTableCreator creator = new DumpToDataTableCreator(dumpInstances);
                creator.addExtractor(extractor);
                DataTable table = creator.createDataTable();
                writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + (i + 1) + "\\" + "Regular\\" + j + "-regular-train+test" + csvName, CsvNumberRepresentation.INTEGER_REPRESENTATION, TRAIN_TEST, 100, 0, 1000000));
                writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + (i + 1) + "\\" + "Binary\\" + j + "-binary-train+test" + csvName, CsvNumberRepresentation.BINARY_REPRESENTATION, TRAIN_TEST, 100, 0, 1000000));
                writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + (i + 1) + "\\" + "TF\\" + j + "-tf-train+test" + csvName, CsvNumberRepresentation.TF_REPRESENTATION, TRAIN_TEST, 100, 0, 1000000));
                writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + (i + 1) + "\\" + "TF-IDF\\" + j + "-tf-idf-train+test" + csvName, CsvNumberRepresentation.TFIDF_REPRESENTATION, TRAIN_TEST, 100, 0, 1000000));
                extractors.add(extractor);
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
        for (Dump dump : dumps) {
            for (DumpInstanceCreator creator : creators) {
                DumpInstance instance = creator.create(dump);
                if (instance != null) {
                    instances.add(instance);
                    break;
                }
            }
        }
        return instances;
    }
}
