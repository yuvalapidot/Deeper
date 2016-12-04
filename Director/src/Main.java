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

public class Main {

    private static final Logger log = LogManager.getLogger(Main.class);

    private static final int upToN = 5;
    private static final String jsonsDirectoryPath = "C:\\Users\\yuval\\Dropbox\\NGrams\\Jsons";
    private static final String csvPath = "C:\\Users\\yuval\\Dropbox\\NGrams\\Results 80\\Experiment";
    private static final String csvName = "-call-gram-data-table.csv";

    private static final DataSet DSB1 = new DataSet("Baseline", "dsB1", 100);
    private static final DataSet DSB2 = new DataSet("Procmon", "dsB2", 100);
    private static final DataSet DSB3 = new DataSet("Avast", "dsB3", 100);
    private static final DataSet DSB4 = new DataSet("Wireshark", "dsB4", 100);
    private static final DataSet DSB5 = new DataSet("Defrag", "dsB5", 100);
    private static final DataSet DSM1 = new DataSet("HiddenTear", "dsM1", 100);
    private static final DataSet DSM2 = new DataSet("Cerber", "dsM2", 100);
    private static final DataSet DSM3 = new DataSet("TeslaCrypt", "dsM3", 100);
    private static final DataSet DSM4 = new DataSet("Vipasana", "dsM4", 100);
    private static final DataSet DSM5 = new DataSet("Chimera", "dsM5", 100);

    private static final int TRAIN_TEST_SPLIT_PERCENTAGE = 80;

    private static final DumpInstanceCreator[][] creators = {
//            // EXPERIMENT 1
//            {
//                    new DumpInstanceCreator(DSB1.getName(), "BENIGN", DSB1.getCount(), 100),
//                    new DumpInstanceCreator(DSB2.getName(), "ANOMALY", DSB2.getCount(), 100),
//                    new DumpInstanceCreator(DSB3.getName(), "ANOMALY", DSB3.getCount(), 100),
//                    new DumpInstanceCreator(DSB4.getName(), "ANOMALY", DSB4.getCount(), 100),
//                    new DumpInstanceCreator(DSB5.getName(), "ANOMALY", DSB5.getCount(), 100),
//                    new DumpInstanceCreator(DSM1.getName(), "ANOMALY", DSM1.getCount(), 100),
//                    new DumpInstanceCreator(DSM2.getName(), "ANOMALY", DSM2.getCount(), 100),
//                    new DumpInstanceCreator(DSM3.getName(), "ANOMALY", DSM3.getCount(), 100),
//                    new DumpInstanceCreator(DSM4.getName(), "ANOMALY", DSM4.getCount(), 100),
//                    new DumpInstanceCreator(DSM5.getName(), "ANOMALY", DSM5.getCount(), 100),
//            },
//            // EXPERIMENT 2
//            {
//                    new DumpInstanceCreator(DSB2.getName(), "BENIGN", DSB2.getCount(), 100),
//                    new DumpInstanceCreator(DSB3.getName(), "BENIGN", DSB3.getCount(), 100),
//                    new DumpInstanceCreator(DSB4.getName(), "BENIGN", DSB4.getCount(), 100),
//                    new DumpInstanceCreator(DSB5.getName(), "BENIGN", DSB5.getCount(), 100),
//                    new DumpInstanceCreator(DSM1.getName(), "MALICIOUS", DSM1.getCount(), 100),
//                    new DumpInstanceCreator(DSM2.getName(), "MALICIOUS", DSM2.getCount(), 100),
//                    new DumpInstanceCreator(DSM3.getName(), "MALICIOUS", DSM3.getCount(), 100),
//                    new DumpInstanceCreator(DSM4.getName(), "MALICIOUS", DSM4.getCount(), 100),
//                    new DumpInstanceCreator(DSM5.getName(), "MALICIOUS", DSM5.getCount(), 100),
//            },
//            // EXPERIMENT 3
//            {
//                    new DumpInstanceCreator(DSB1.getName(), DSB1.getSymbol(), DSB1.getCount(), 100),
//                    new DumpInstanceCreator(DSB2.getName(), DSB2.getSymbol(), DSB2.getCount(), 100),
//                    new DumpInstanceCreator(DSB3.getName(), DSB3.getSymbol(), DSB3.getCount(), 100),
//                    new DumpInstanceCreator(DSB4.getName(), DSB4.getSymbol(), DSB4.getCount(), 100),
//                    new DumpInstanceCreator(DSB5.getName(), DSB5.getSymbol(), DSB5.getCount(), 100),
//                    new DumpInstanceCreator(DSM1.getName(), DSM1.getSymbol(), DSM1.getCount(), 100),
//                    new DumpInstanceCreator(DSM2.getName(), DSM2.getSymbol(), DSM2.getCount(), 100),
//                    new DumpInstanceCreator(DSM3.getName(), DSM3.getSymbol(), DSM3.getCount(), 100),
//                    new DumpInstanceCreator(DSM4.getName(), DSM4.getSymbol(), DSM4.getCount(), 100),
//                    new DumpInstanceCreator(DSM5.getName(), DSM5.getSymbol(), DSM5.getCount(), 100),
//            },
//            // EXPERIMENT 4.1
//            {
//                    new DumpInstanceCreator(DSB2.getName(), "BENIGN", DSB2.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSB3.getName(), "BENIGN", DSB3.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSB4.getName(), "BENIGN", DSB4.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSB5.getName(), "BENIGN", DSB5.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSM1.getName(), "MALICIOUS", DSM1.getCount(), 0),
//                    new DumpInstanceCreator(DSM2.getName(), "MALICIOUS", DSM2.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSM3.getName(), "MALICIOUS", DSM3.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSM4.getName(), "MALICIOUS", DSM4.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSM5.getName(), "MALICIOUS", DSM5.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//            },
//            // EXPERIMENT 4.2
//            {
//                    new DumpInstanceCreator(DSB2.getName(), "BENIGN", DSB2.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSB3.getName(), "BENIGN", DSB3.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSB4.getName(), "BENIGN", DSB4.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSB5.getName(), "BENIGN", DSB5.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSM1.getName(), "MALICIOUS", DSM1.getCount(), 0),
//                    new DumpInstanceCreator(DSM2.getName(), "MALICIOUS", DSM2.getCount(), 100),
//                    new DumpInstanceCreator(DSM3.getName(), "MALICIOUS", DSM3.getCount(), 100),
//                    new DumpInstanceCreator(DSM4.getName(), "MALICIOUS", DSM4.getCount(), 100),
//                    new DumpInstanceCreator(DSM5.getName(), "MALICIOUS", DSM5.getCount(), 100),
//            },
            // EXPERIMENT 4.3
            {
                    new DumpInstanceCreator(DSB1.getName(), "BENIGN", DSB1.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
                    new DumpInstanceCreator(DSB2.getName(), "BENIGN", DSB2.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
                    new DumpInstanceCreator(DSB3.getName(), "BENIGN", DSB3.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
                    new DumpInstanceCreator(DSB4.getName(), "BENIGN", DSB4.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
                    new DumpInstanceCreator(DSB5.getName(), "BENIGN", DSB5.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
                    new DumpInstanceCreator(DSM1.getName(), "MALICIOUS", DSM1.getCount(), 0),
                    new DumpInstanceCreator(DSM2.getName(), "MALICIOUS", DSM2.getCount(), 100),
                    new DumpInstanceCreator(DSM3.getName(), "MALICIOUS", DSM3.getCount(), 100),
                    new DumpInstanceCreator(DSM4.getName(), "MALICIOUS", DSM4.getCount(), 100),
                    new DumpInstanceCreator(DSM5.getName(), "MALICIOUS", DSM5.getCount(), 100),
            },
            // EXPERIMENT 5.1
//            {
//                    new DumpInstanceCreator(DSB2.getName(), "BENIGN", DSB2.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSB3.getName(), "BENIGN", DSB3.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSB4.getName(), "BENIGN", DSB4.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSB5.getName(), "BENIGN", DSB5.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSM1.getName(), "MALICIOUS", DSM1.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSM2.getName(), "MALICIOUS", DSM2.getCount(), 0),
//                    new DumpInstanceCreator(DSM3.getName(), "MALICIOUS", DSM3.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSM4.getName(), "MALICIOUS", DSM4.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSM5.getName(), "MALICIOUS", DSM5.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//            },
//            // EXPERIMENT 5.2
//            {
//                    new DumpInstanceCreator(DSB2.getName(), "BENIGN", DSB2.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSB3.getName(), "BENIGN", DSB3.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSB4.getName(), "BENIGN", DSB4.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSB5.getName(), "BENIGN", DSB5.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSM1.getName(), "MALICIOUS", DSM1.getCount(), 100),
//                    new DumpInstanceCreator(DSM2.getName(), "MALICIOUS", DSM2.getCount(), 0),
//                    new DumpInstanceCreator(DSM3.getName(), "MALICIOUS", DSM3.getCount(), 100),
//                    new DumpInstanceCreator(DSM4.getName(), "MALICIOUS", DSM4.getCount(), 100),
//                    new DumpInstanceCreator(DSM5.getName(), "MALICIOUS", DSM5.getCount(), 100),
//            },
            // EXPERIMENT 5.3
            {
                    new DumpInstanceCreator(DSB1.getName(), "BENIGN", DSB1.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
                    new DumpInstanceCreator(DSB2.getName(), "BENIGN", DSB2.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
                    new DumpInstanceCreator(DSB3.getName(), "BENIGN", DSB3.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
                    new DumpInstanceCreator(DSB4.getName(), "BENIGN", DSB4.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
                    new DumpInstanceCreator(DSB5.getName(), "BENIGN", DSB5.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
                    new DumpInstanceCreator(DSM1.getName(), "MALICIOUS", DSM1.getCount(), 100),
                    new DumpInstanceCreator(DSM2.getName(), "MALICIOUS", DSM2.getCount(), 0),
                    new DumpInstanceCreator(DSM3.getName(), "MALICIOUS", DSM3.getCount(), 100),
                    new DumpInstanceCreator(DSM4.getName(), "MALICIOUS", DSM4.getCount(), 100),
                    new DumpInstanceCreator(DSM5.getName(), "MALICIOUS", DSM5.getCount(), 100),
            },
            // EXPERIMENT 6.1
//            {
//                    new DumpInstanceCreator(DSB2.getName(), "BENIGN", DSB2.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSB3.getName(), "BENIGN", DSB3.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSB4.getName(), "BENIGN", DSB4.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSB5.getName(), "BENIGN", DSB5.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSM1.getName(), "MALICIOUS", DSM1.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSM2.getName(), "MALICIOUS", DSM2.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSM3.getName(), "MALICIOUS", DSM3.getCount(), 0),
//                    new DumpInstanceCreator(DSM4.getName(), "MALICIOUS", DSM4.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSM5.getName(), "MALICIOUS", DSM5.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//            },
//            // EXPERIMENT 6.2
//            {
//                    new DumpInstanceCreator(DSB2.getName(), "BENIGN", DSB2.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSB3.getName(), "BENIGN", DSB3.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSB4.getName(), "BENIGN", DSB4.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSB5.getName(), "BENIGN", DSB5.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSM1.getName(), "MALICIOUS", DSM1.getCount(), 100),
//                    new DumpInstanceCreator(DSM2.getName(), "MALICIOUS", DSM2.getCount(), 100),
//                    new DumpInstanceCreator(DSM3.getName(), "MALICIOUS", DSM3.getCount(), 0),
//                    new DumpInstanceCreator(DSM4.getName(), "MALICIOUS", DSM4.getCount(), 100),
//                    new DumpInstanceCreator(DSM5.getName(), "MALICIOUS", DSM5.getCount(), 100),
//            },
            // EXPERIMENT 6.3
            {
                    new DumpInstanceCreator(DSB1.getName(), "BENIGN", DSB1.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
                    new DumpInstanceCreator(DSB2.getName(), "BENIGN", DSB2.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
                    new DumpInstanceCreator(DSB3.getName(), "BENIGN", DSB3.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
                    new DumpInstanceCreator(DSB4.getName(), "BENIGN", DSB4.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
                    new DumpInstanceCreator(DSB5.getName(), "BENIGN", DSB5.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
                    new DumpInstanceCreator(DSM1.getName(), "MALICIOUS", DSM1.getCount(), 100),
                    new DumpInstanceCreator(DSM2.getName(), "MALICIOUS", DSM2.getCount(), 100),
                    new DumpInstanceCreator(DSM3.getName(), "MALICIOUS", DSM3.getCount(), 0),
                    new DumpInstanceCreator(DSM4.getName(), "MALICIOUS", DSM4.getCount(), 100),
                    new DumpInstanceCreator(DSM5.getName(), "MALICIOUS", DSM5.getCount(), 100),
            },
            // EXPERIMENT 7.1
//            {
//                    new DumpInstanceCreator(DSB2.getName(), "BENIGN", DSB2.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSB3.getName(), "BENIGN", DSB3.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSB4.getName(), "BENIGN", DSB4.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSB5.getName(), "BENIGN", DSB5.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSM1.getName(), "MALICIOUS", DSM1.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSM2.getName(), "MALICIOUS", DSM2.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSM3.getName(), "MALICIOUS", DSM3.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSM4.getName(), "MALICIOUS", DSM4.getCount(), 0),
//                    new DumpInstanceCreator(DSM5.getName(), "MALICIOUS", DSM5.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//            },
//            // EXPERIMENT 7.2
//            {
//                    new DumpInstanceCreator(DSB2.getName(), "BENIGN", DSB2.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSB3.getName(), "BENIGN", DSB3.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSB4.getName(), "BENIGN", DSB4.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSB5.getName(), "BENIGN", DSB5.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSM1.getName(), "MALICIOUS", DSM1.getCount(), 100),
//                    new DumpInstanceCreator(DSM2.getName(), "MALICIOUS", DSM2.getCount(), 100),
//                    new DumpInstanceCreator(DSM3.getName(), "MALICIOUS", DSM3.getCount(), 100),
//                    new DumpInstanceCreator(DSM4.getName(), "MALICIOUS", DSM4.getCount(), 0),
//                    new DumpInstanceCreator(DSM5.getName(), "MALICIOUS", DSM5.getCount(), 100),
//            },
            // EXPERIMENT 7.3
            {
                    new DumpInstanceCreator(DSB1.getName(), "BENIGN", DSB1.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
                    new DumpInstanceCreator(DSB2.getName(), "BENIGN", DSB2.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
                    new DumpInstanceCreator(DSB3.getName(), "BENIGN", DSB3.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
                    new DumpInstanceCreator(DSB4.getName(), "BENIGN", DSB4.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
                    new DumpInstanceCreator(DSB5.getName(), "BENIGN", DSB5.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
                    new DumpInstanceCreator(DSM1.getName(), "MALICIOUS", DSM1.getCount(), 100),
                    new DumpInstanceCreator(DSM2.getName(), "MALICIOUS", DSM2.getCount(), 100),
                    new DumpInstanceCreator(DSM3.getName(), "MALICIOUS", DSM3.getCount(), 100),
                    new DumpInstanceCreator(DSM4.getName(), "MALICIOUS", DSM4.getCount(), 0),
                    new DumpInstanceCreator(DSM5.getName(), "MALICIOUS", DSM5.getCount(), 100),
            },
            // EXPERIMENT 8.1
//            {
//                    new DumpInstanceCreator(DSB2.getName(), "BENIGN", DSB2.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSB3.getName(), "BENIGN", DSB3.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSB4.getName(), "BENIGN", DSB4.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSB5.getName(), "BENIGN", DSB5.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSM1.getName(), "MALICIOUS", DSM1.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSM2.getName(), "MALICIOUS", DSM2.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSM3.getName(), "MALICIOUS", DSM3.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSM4.getName(), "MALICIOUS", DSM4.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSM5.getName(), "MALICIOUS", DSM5.getCount(), 0),
//            },
//            // EXPERIMENT 8.2
//            {
//                    new DumpInstanceCreator(DSB2.getName(), "BENIGN", DSB2.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSB3.getName(), "BENIGN", DSB3.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSB4.getName(), "BENIGN", DSB4.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSB5.getName(), "BENIGN", DSB5.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
//                    new DumpInstanceCreator(DSM1.getName(), "MALICIOUS", DSM1.getCount(), 100),
//                    new DumpInstanceCreator(DSM2.getName(), "MALICIOUS", DSM2.getCount(), 100),
//                    new DumpInstanceCreator(DSM3.getName(), "MALICIOUS", DSM3.getCount(), 100),
//                    new DumpInstanceCreator(DSM4.getName(), "MALICIOUS", DSM4.getCount(), 100),
//                    new DumpInstanceCreator(DSM5.getName(), "MALICIOUS", DSM5.getCount(), 0),
//            },
            // EXPERIMENT 8.3
            {
                    new DumpInstanceCreator(DSB1.getName(), "BENIGN", DSB1.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
                    new DumpInstanceCreator(DSB2.getName(), "BENIGN", DSB2.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
                    new DumpInstanceCreator(DSB3.getName(), "BENIGN", DSB3.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
                    new DumpInstanceCreator(DSB4.getName(), "BENIGN", DSB4.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
                    new DumpInstanceCreator(DSB5.getName(), "BENIGN", DSB5.getCount(), TRAIN_TEST_SPLIT_PERCENTAGE),
                    new DumpInstanceCreator(DSM1.getName(), "MALICIOUS", DSM1.getCount(), 100),
                    new DumpInstanceCreator(DSM2.getName(), "MALICIOUS", DSM2.getCount(), 100),
                    new DumpInstanceCreator(DSM3.getName(), "MALICIOUS", DSM3.getCount(), 100),
                    new DumpInstanceCreator(DSM4.getName(), "MALICIOUS", DSM4.getCount(), 100),
                    new DumpInstanceCreator(DSM5.getName(), "MALICIOUS", DSM5.getCount(), 0),
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
            int trainCounter = 0;
            for (DumpInstance instance : dumpInstances) {
                if (instance.getSetType().equals(InstanceSetType.TRAIN_SET)) {
                    trainCounter++;
                }
            }
            log.info("Experiment " + (i + 1) + " train percentage: " + (((double) trainCounter) / dumpInstances.size()) * 100);
            for (int j = 1; j <= upToN; j++) {
                IFeatureExtractor<DumpInstance> extractor = new CallGramExtractor(j);
                DataTableCreator creator = new DumpToDataTableCreator(dumpInstances);
                creator.addExtractor(extractor);
                DataTable table = creator.createDataTable();
//            writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + (i + 1) + "\\" + "Regular\\" + j + "-regular-train" + csvName, CsvNumberRepresentation.INTEGER_REPRESENTATION, TRAIN));
//            writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + (i + 1) + "\\" + "Regular\\" + j + "-regular-test" + csvName, CsvNumberRepresentation.INTEGER_REPRESENTATION, TEST));
                writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + (i + 1) + "\\" + "Regular\\" + j + "-regular-train+test" + csvName, CsvNumberRepresentation.INTEGER_REPRESENTATION, TRAIN_TEST));
//            writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + (i + 1) + "\\" + "Binary\\" + j + "-binary-train" + csvName, CsvNumberRepresentation.BINARY_REPRESENTATION, TRAIN));
//            writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + (i + 1) + "\\" + "Binary\\" + j + "-binary-test" + csvName, CsvNumberRepresentation.BINARY_REPRESENTATION, TEST));
                writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + (i + 1) + "\\" + "Binary\\" + j + "-binary-train+test" + csvName, CsvNumberRepresentation.BINARY_REPRESENTATION, TRAIN_TEST));
//            writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + (i + 1) + "\\" + "TF\\" + j + "-tf-train" + csvName, CsvNumberRepresentation.TF_REPRESENTATION, TRAIN));
//            writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + (i + 1) + "\\" + "TF\\" + j + "-tf-test" + csvName, CsvNumberRepresentation.TF_REPRESENTATION, TEST));
                writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + (i + 1) + "\\" + "TF\\" + j + "-tf-train+test" + csvName, CsvNumberRepresentation.TF_REPRESENTATION, TRAIN_TEST));
//            writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + (i + 1) + "\\" + "TF-IDF\\" + j + "-tf-idf-train" + csvName, CsvNumberRepresentation.TFIDF_REPRESENTATION, TRAIN));
//            writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + (i + 1) + "\\" + "TF-IDF\\" + j + "-tf-idf-test" + csvName, CsvNumberRepresentation.TFIDF_REPRESENTATION, TEST));
                writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + (i + 1) + "\\" + "TF-IDF\\" + j + "-tf-idf-train+test" + csvName, CsvNumberRepresentation.TFIDF_REPRESENTATION, TRAIN_TEST));
                extractors.add(extractor);
            }
//            DataTableCreator creator = new DumpToDataTableCreator(dumpInstances, extractors);
//            DataTable table = creator.createDataTable();
//        writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + (i + 1) + "\\" + "Regular\\combined-regular-train-1-" + upToN + csvName, CsvNumberRepresentation.INTEGER_REPRESENTATION, TRAIN));
//        writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + (i + 1) + "\\" + "Regular\\combined-regular-test-1-" + upToN + csvName, CsvNumberRepresentation.INTEGER_REPRESENTATION, TEST));
//            writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + (i + 1) + "\\" + "Regular\\combined-regular-train+test-1-" + upToN + csvName, CsvNumberRepresentation.INTEGER_REPRESENTATION, TRAIN_TEST));
//        writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + (i + 1) + "\\" + "Binary\\combined-binary-train-1-" + upToN + csvName, CsvNumberRepresentation.BINARY_REPRESENTATION, TRAIN));
//        writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + (i + 1) + "\\" + "Binary\\combined-binary-test-1-" + upToN + csvName, CsvNumberRepresentation.BINARY_REPRESENTATION, TEST));
//            writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + (i + 1) + "\\" + "Binary\\combined-binary-train+test-1-" + upToN + csvName, CsvNumberRepresentation.BINARY_REPRESENTATION, TRAIN_TEST));
//        writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + (i + 1) + "\\" + "TF\\combined-tf-train-1-" + upToN + csvName, CsvNumberRepresentation.TF_REPRESENTATION, TRAIN));
//        writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + (i + 1) + "\\" + "TF\\combined-tf-test-1-" + upToN + csvName, CsvNumberRepresentation.TF_REPRESENTATION, TEST));
//            writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + (i + 1) + "\\" + "TF\\combined-tf-train+test-1-" + upToN + csvName, CsvNumberRepresentation.TF_REPRESENTATION, TRAIN_TEST));
//        writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + (i + 1) + "\\" + "TF-IDF\\combined-tf-idf-train-1-" + upToN + csvName, CsvNumberRepresentation.TFIDF_REPRESENTATION, TRAIN));
//        writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + (i + 1) + "\\" + "TF-IDF\\combined-tf-idf-test-1-" + upToN + csvName, CsvNumberRepresentation.TFIDF_REPRESENTATION, TEST));
//            writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath + " " + (i + 1) + "\\" + "TF-IDF\\combined-tf-idf-train+test-1-" + upToN + csvName, CsvNumberRepresentation.TFIDF_REPRESENTATION, TRAIN_TEST));
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
