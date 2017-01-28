import creator.DataTableCreator;
import creator.DumpToDataTableCreator;
import extractor.IFeatureExtractor;
import extractor.SequenceExtractor;
import model.data.DataTable;
import model.feature.Feature;
import model.instance.DumpInstance;
import model.instance.Instance;
import model.instance.InstanceSetType;
import model.memory.Dump;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ranker.Ranker;
import ranker.rankers.FishersScoreRanker;
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

public class MainSequencesExperiment2 {

    private static final String jsonsDirectoryPath = "D:\\Dropbox\\NGrams\\Jsons";
    private static final String csvPath = "D:\\Dropbox\\NGrams\\Results\\Sequences\\Experiemnt 2\\";

    private static final int minimumSupport = 101;
    private static final int maximumSupport = 5000;
    private static final int minimumSequenceLength = 2;
    private static final int maximumSequenceLength = 4;
    private static final int batchSize = 100;

    private static final String[] BenignNames = { "Baseline", "Defrag", "Procmon", "Avast", "Wireshark" };
    private static final String[] MaliciousNames = { "HiddenTear", "Cerber", "TeslaCrypt", "Vipasana", "Chimera"};

    private static final Set<InstanceSetType> TRAIN_TEST = new LinkedHashSet<>(Arrays.asList(InstanceSetType.TRAIN_SET, InstanceSetType.TEST_SET));

    private static final Logger log = LogManager.getLogger(MainSequencesToDB.class);

    public static void main(String[] args) throws IOException {
        List<Dump> dumps = getDumps(getJsonFiles());
        DumpInstanceCreator[] creators = creatorsForExperiment();
        List<DumpInstance> dumpInstances = getDumpInstances(dumps, creators);
        Feature.initialize(dumpInstances);
        IFeatureExtractor<DumpInstance> extractor = new SequenceExtractor(minimumSupport, maximumSupport, minimumSequenceLength, maximumSequenceLength, batchSize);
        DataTableCreator creator = new DumpToDataTableCreator(dumpInstances);
        creator.addExtractor(extractor);
        DataTable table = creator.createDataTable();
        TD4CDiscretizator klDiscretizator = new TD4CDiscretizator(new LinkedHashSet<>(dumpInstances), new KullbackLeiblerDistance());
        TD4CDiscretizator entropyDiscretizator = new TD4CDiscretizator(new LinkedHashSet<>(dumpInstances), new EntropyDistance());
        TD4CDiscretizator cosineDiscretizator = new TD4CDiscretizator(new LinkedHashSet<>(dumpInstances), new CosineDistance());
        Ranker ranker = new Ranker(new FishersScoreRanker());
        for (String maliciousToTest : MaliciousNames) {
            changeTestInstances(table, maliciousToTest);
            discreteAndWrite(table, klDiscretizator, 3, "kl", maliciousToTest);
            discreteAndWrite(table, klDiscretizator, 5, "kl", maliciousToTest);
            discreteAndWrite(table, entropyDiscretizator, 3, "entropy", maliciousToTest);
            discreteAndWrite(table, entropyDiscretizator, 5, "entropy", maliciousToTest);
            discreteAndWrite(table, cosineDiscretizator, 3, "cosine", maliciousToTest);
            discreteAndWrite(table, cosineDiscretizator, 5, "cosine", maliciousToTest);
            rankAndWrite(table, ranker, maliciousToTest);
        }
    }

    private static DumpInstanceCreator[] creatorsForExperiment() {
        DumpInstanceCreator[] creators = new DumpInstanceCreator[BenignNames.length + MaliciousNames.length];
        for (int i = 0; i < MaliciousNames.length; i++) {
            creators[BenignNames.length + i] = new DumpInstanceCreator(MaliciousNames[i], "MALICIOUS", batchSize, 100);
        }
        for (int i = 0; i < BenignNames.length; i++) {
            creators[i] = new DumpInstanceCreator(BenignNames[i], "BENIGN", batchSize, 80);
        }
        return creators;
    }

    private static void changeTestInstances(DataTable table, String maliciousToTest) {
        for (Instance instance : table.getInstances()) {
            if (instance.getClassification().equals("MALICIOUS")) {
                if (instance.getType().equals(maliciousToTest)) {
                    instance.setSetType(InstanceSetType.TEST_SET);
                } else {
                    instance.setSetType(InstanceSetType.TRAIN_SET);
                }
            }
        }
    }

    private static void discreteAndWrite(DataTable table, TD4CDiscretizator discretizator, int bins, String sign, String testedMalicious) throws IOException {
        DataTableCsvWriter writer = new DataTableCsvWriter();
        DataTable discreteTable = discretizator.discrete(table, bins, 1);
        writer.dataTableToCsv(new DataTableToCsvRequest(discreteTable, csvPath + testedMalicious + " Tested\\min_support=" + minimumSupport + "-max_support=" + maximumSupport + "-sequence_length=" + minimumSequenceLength + "-" + maximumSequenceLength + "-" + sign + "-" + bins + "-bins.csv", CsvNumberRepresentation.INTEGER_REPRESENTATION, TRAIN_TEST, 100, 1));
    }

    private static void rankAndWrite(DataTable table, Ranker ranker, String testedMalicious) throws IOException {
        DataTableCsvWriter writer = new DataTableCsvWriter();
        DataTable rankedTable = ranker.rankTable(table, 1);
        writer.dataTableToCsv(new DataTableToCsvRequest(rankedTable, csvPath + testedMalicious + " Tested\\min_support=" + minimumSupport + "-max_support=" + maximumSupport + "-sequence_length=" + minimumSequenceLength + "-" + maximumSequenceLength + "-ranked.csv", CsvNumberRepresentation.INTEGER_REPRESENTATION, TRAIN_TEST, 100, 1));
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
