package writer;

import api.WinDbgAPI;
import com.fasterxml.jackson.databind.ObjectMapper;
import memory.model.Dump;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JsonDumpWriter {

    public void dumpToJson(DumpToJsonRequest request) throws IOException {
        File file = new File(request.getJsonPath());
        if (request.isSkipIfExists() & file.exists()) {
            return;
        }
        Dump dump = WinDbgAPI.getDump(request.getDumpPath(), request.getDumpClassification());
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(file, dump);
    }

    public void dumpsToJsons(List<DumpToJsonRequest> requests, boolean parallel) throws IOException {
        if (parallel) {

        } else {
            for (DumpToJsonRequest request : requests) {
                dumpToJson(request);
            }
        }
    }

}
