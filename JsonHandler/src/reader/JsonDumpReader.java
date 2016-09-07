package reader;

import com.fasterxml.jackson.databind.ObjectMapper;
import memory.model.Dump;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonDumpReader {

    public Dump jsonToDump(JsonToDumpRequest request) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(request.getJsonPath()), Dump.class);
    }

    public List<Dump> jsonsToDumps(List<JsonToDumpRequest> requests) throws IOException {
        List<Dump> dumps = new ArrayList<Dump>();
        for (JsonToDumpRequest request : requests) {
            dumps.add(jsonToDump(request));
        }
        return dumps;
    }
}
