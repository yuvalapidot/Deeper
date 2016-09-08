import creator.SystemCallGramCreator;
import extractor.WinDbgExtractor;
import model.memory.CallGramsTable;
import model.memory.Dump;
import writer.CsvWriter;

import java.util.List;

/**
 * Created by yuval on 06/09/2016.
 */
public class Main {

//    private static String benignDirectoryPath = "D:\\Snapshots\\Benign";
//    private static String maliciousDirectoryPath = "D:\\Snapshots\\Malicious";
    private static String dmpDirectoryPath = "D:\\DeepFeaturesExperiment\\Dumps";
    private static String outputCsvFilePath = "D:\\DeepFeaturesExperiment\\Results\\output_100Benign_100ProcMon_449Malicious_1+2+3+4.csv";

    public static void main(String[] args) throws Exception {
        WinDbgExtractor extractor = new WinDbgExtractor();
        List<Dump> dumps = extractor.getDumpsFromDirectory(dmpDirectoryPath);
        SystemCallGramCreator callGramCreator = new SystemCallGramCreator();
        CallGramsTable table = callGramCreator.generateUpToNGrams(dumps, 1, 4);
        CsvWriter writer = new CsvWriter();
        writer.writeCallGramsTableToCsv(table, outputCsvFilePath);
    }
}
