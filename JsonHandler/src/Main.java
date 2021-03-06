import writer.DumpToJsonRequest;
import writer.JsonDumpWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final String dumpsDirectory = "\\\\tdata.campus.ad.bgu.ac.il\\MalwareLab\\Memory Dumps\\EMC\\DMPs\\_4_Email_Baseline1";

    public static void main(String[] args) throws IOException, InterruptedException {
        JsonDumpWriter writer = new JsonDumpWriter();
        writer.dumpsToJsons(getDumpToJsonRequestsFromDirectory(dumpsDirectory), true);
    }

    private static List<DumpToJsonRequest> getDumpToJsonRequestsFromDirectory(String path) throws IOException {
        List<DumpToJsonRequest> requests = new ArrayList<>();
        java.nio.file.Files.walk(Paths.get(path)).filter(filePath -> Files.isRegularFile(filePath)).forEach(filePath -> {
            String dumpPath = filePath.toString();
            if (!dumpPath.equals(path)) {
                requests.add(new DumpToJsonRequest(dumpPath, dumpPath.replace(dumpsDirectory, "D:\\Dropbox\\Deeper\\All Jsons\\_4_Email_Baseline1").replace(".dmp", ".json"), true));
            }
        });
        return requests;
    }
}
