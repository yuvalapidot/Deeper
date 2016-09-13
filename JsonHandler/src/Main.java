import writer.DumpToJsonRequest;
import writer.JsonDumpWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final String benignDirectory = "D:\\DeepFeaturesExperiment\\Dumps\\Benign";
    private static final String maliciousDirectory = "D:\\DeepFeaturesExperiment\\Dumps\\Malicious";

    public static void main(String[] args) throws IOException, InterruptedException {
        JsonDumpWriter writer = new JsonDumpWriter();
        writer.dumpsToJsons(getDumpToJsonRequestsFromDirectory(benignDirectory, "Benign"), true);
        writer.dumpsToJsons(getDumpToJsonRequestsFromDirectory(maliciousDirectory, "Malicious"), true);
    }

    private static List<DumpToJsonRequest> getDumpToJsonRequestsFromDirectory(String path, String classification) throws IOException {
        List<DumpToJsonRequest> requests = new ArrayList<>();
        java.nio.file.Files.walk(Paths.get(path)).filter(filePath -> Files.isRegularFile(filePath)).forEach(filePath -> {
            String dumpPath = filePath.toString();
            if (!dumpPath.equals(path)) {
                requests.add(new DumpToJsonRequest(dumpPath, dumpPath.replace("Dumps", "Jsons").replace(".dmp", ".json"), classification, true));
            }
        });
        return requests;
    }
}
