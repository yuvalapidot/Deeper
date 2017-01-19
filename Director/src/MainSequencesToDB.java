import creator.DataTableCreator;
import creator.DumpToDataTableCreator;
import dal.sql.sqlite.DataAccessLayer;
import dal.sql.sqlite.IDataAccessLayer;
import extractor.IFeatureExtractor;
import extractor.SequenceExtractor;
import model.instance.DumpInstance;
import model.memory.Dump;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reader.JsonDumpReader;
import reader.JsonToDumpRequest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainSequencesToDB {

    private static final String jsonsDirectoryPath = "D:\\Dropbox\\NGrams\\Jsons";

    private static final int minimumSupport = 101;
    private static final int maximumSupport = 5000;
    private static final int minimumSequenceLength = 2;
    private static final int maximumSequenceLength = 5;
    private static final int batchSize = 100;

    private static final String[] BenignNames = {"Baseline", "Procmon", "Avast", "Wireshark", "Defrag"};
    private static final String[] MaliciousNames = {"HiddenTear", "Cerber", "TeslaCrypt", "Vipasana", "Chimera"};

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
        try (IDataAccessLayer dal = new DataAccessLayer()) {
            dal.createDumpsTable();
            dal.createSequenceTable();
            dal.createDumpSequenceTable();
            dal.insertDumps(dumpInstances);
        } catch (SQLException | IOException ex) {
            log.error("Encountered an error", ex);
            ex.printStackTrace();
        }
        IFeatureExtractor<DumpInstance> extractor = new SequenceExtractor(minimumSupport, maximumSupport, minimumSequenceLength, maximumSequenceLength, batchSize);
        DataTableCreator creator = new DumpToDataTableCreator(dumpInstances);
        creator.addExtractor(extractor);
        creator.createDataTableToDataBase();
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
