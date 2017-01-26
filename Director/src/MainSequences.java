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
import td4c.TD4CDiscretizator;
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

public class MainSequences {

    private static final String jsonsDirectoryPath = "D:\\Dropbox\\NGrams\\Jsons";
    private static final String csvPath = "D:\\Dropbox\\NGrams\\Results\\Sequences\\";

    private static final int minimumSupport = 101;
    private static final int maximumSupport = 5000;
    private static final int minimumSequenceLength = 4;
    private static final int maximumSequenceLength = 4;
    private static final int batchSize = 100;

    private static final String[] BenignNames = { "Defrag", "Baseline", "Procmon", "Avast", "Wireshark" };
    private static final String[] MaliciousNames = { "HiddenTear", "Cerber", "TeslaCrypt", "Vipasana", "Chimera"};

    private static final Set<InstanceSetType> TRAIN_TEST = new LinkedHashSet<>(Arrays.asList(InstanceSetType.TRAIN_SET, InstanceSetType.TEST_SET));

    private static final Logger log = LogManager.getLogger(MainSequencesToDB.class);

    public static void main(String[] args) throws IOException {
        List<Dump> dumps = getDumps(getJsonFiles());
        DumpInstanceCreator[] creators = new DumpInstanceCreator[BenignNames.length + MaliciousNames.length];
        for (int i = 0; i < BenignNames.length; i++) {
                creators[i] = new DumpInstanceCreator(BenignNames[i], "BENIGN", batchSize, 100);
        }
        for (int i = 0; i < MaliciousNames.length; i++) {
                creators[BenignNames.length + i] = new DumpInstanceCreator(MaliciousNames[i], "MALICIOUS", batchSize, 100);
        }
        List<DumpInstance> dumpInstances = getDumpInstances(dumps, creators);
        IFeatureExtractor<DumpInstance> extractor = new SequenceExtractor(minimumSupport, maximumSupport, minimumSequenceLength, maximumSequenceLength, batchSize);
        DataTableCreator creator = new DumpToDataTableCreator(dumpInstances);
        creator.addExtractor(extractor);
        DataTable table = creator.createDataTable();
        TD4CDiscretizator klDiscretizator = new TD4CDiscretizator(new LinkedHashSet<>(dumpInstances), new KullbackLeiblerDistance());
        TD4CDiscretizator entropyDiscretizator = new TD4CDiscretizator(new LinkedHashSet<>(dumpInstances), new KullbackLeiblerDistance());
        TD4CDiscretizator cosineDiscretizator = new TD4CDiscretizator(new LinkedHashSet<>(dumpInstances), new KullbackLeiblerDistance());
        DataTableCsvWriter writer = new DataTableCsvWriter();
        DataTable klTable3 = klDiscretizator.discrete(table, 3);
        writer.dataTableToCsv(new DataTableToCsvRequest(klTable3, csvPath + "kl-3-bins.csv", CsvNumberRepresentation.INTEGER_REPRESENTATION, TRAIN_TEST, 1, 0.2));
        DataTable klTable5 = klDiscretizator.discrete(table, 5);
        writer.dataTableToCsv(new DataTableToCsvRequest(klTable5, csvPath + "kl-5-bins.csv", CsvNumberRepresentation.INTEGER_REPRESENTATION, TRAIN_TEST, 1, 0.2));
        DataTable entropyTable3 = entropyDiscretizator.discrete(table, 3);
        writer.dataTableToCsv(new DataTableToCsvRequest(entropyTable3, csvPath + "entropy-3-bins.csv", CsvNumberRepresentation.INTEGER_REPRESENTATION, TRAIN_TEST, 1, 0.2));
        DataTable entropyTable5 = entropyDiscretizator.discrete(table, 5);
        writer.dataTableToCsv(new DataTableToCsvRequest(entropyTable5, csvPath + "entropy-5-bins.csv", CsvNumberRepresentation.INTEGER_REPRESENTATION, TRAIN_TEST, 1, 0.2));
        DataTable cosineTable3 = cosineDiscretizator.discrete(table, 3);
        writer.dataTableToCsv(new DataTableToCsvRequest(cosineTable3, csvPath + "cosine-3-bins.csv", CsvNumberRepresentation.INTEGER_REPRESENTATION, TRAIN_TEST, 1, 0.2));
        DataTable cosineTable5 = cosineDiscretizator.discrete(table, 5);
        writer.dataTableToCsv(new DataTableToCsvRequest(cosineTable5, csvPath + "cosine-5-bins.csv", CsvNumberRepresentation.INTEGER_REPRESENTATION, TRAIN_TEST, 1, 0.2));
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
            int timestamp = 0;
            for (Dump dump : dumps) {
                DumpInstance instance = creator.create(dump);
                if (instance != null) {
                    instance.setTimestamp(timestamp++);
                    instances.add(instance);
                }
            }
        }
        return instances;
    }

}