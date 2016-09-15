package reader;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.memory.Dump;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonDumpReader {

    private final Logger log = LogManager.getLogger(JsonDumpReader.class);

    /**
     * Converting dump json into dump according to request.
     * Notify any observers on request.
     * @param request JsonToDumpRequest.
     * @return Dump represented by the json file.
     * @throws IOException if encounters error while parsing the json file.
     */
    public Dump jsonToDump(JsonToDumpRequest request) throws IOException {
        log.info("Trying to parse JSON: " + request.getJsonPath() + " into Dump");
        ObjectMapper mapper = new ObjectMapper();
        Dump dump;
        try {
            dump = mapper.readValue(request.getFile(), Dump.class);
        } catch (IOException e) {
            log.error("Encountered an error while trying to parse JSON: " + request.getJsonPath() + " into Dump", e);
            throw e;
        }
        log.info("Finished parsing JSON: " + request.getJsonPath() + " into Dump");
        request.notifyObservers(dump);
        return dump;
    }

    /**
     * Converting json files into dumps according to requests.
     * @param requests List of JsonToDumpRequests.
     * @return List of dumps represented by the json files.
     */
    public List<Dump> jsonsToDumps(List<JsonToDumpRequest> requests) {
        List<Dump> dumps = new ArrayList<>();
        log.info("Trying to parse " + requests.size() + " JSONs into Dumps");
        for (JsonToDumpRequest request : requests) {
            try {
                dumps.add(jsonToDump(request));
            } catch (IOException e) {
                log.warn("Failed to parse JSON " + request.getJsonPath() + " into Dump. Will continue with other JSONs");
            }
        }
        log.info("Finished parsing " + dumps.size() + " JSONs into Dumps");
        return dumps;
    }
}
