package writer;

import api.WinDbgAPI;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.memory.Dump;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class JsonDumpWriter {

    private static final long DUMP_PROCESS_TIME = 10;
    private final Logger log = LogManager.getLogger(JsonDumpWriter.class);

    /**
     * Converting dump file into json file according to request.
     * Notify any observers on request.
     * @param request DumpToJsonRequest.
     * @throws IOException if reading dump or writing to json fails.
     */
    public void dumpToJson(DumpToJsonRequest request) throws IOException {
        log.info("Trying to parse Dump: " + request.getDumpPath() + " into JSON: " + request.getJsonPath());
        File file = new File(request.getJsonPath());
        file.getParentFile().mkdirs();
        if (request.isSkipIfExists() & file.exists()) {
            log.info("JSON file: " + request.getJsonPath() + " already exists. Skipping Dump: " + request.getDumpPath());
            return;
        }
        Dump dump;
        try {
            dump = WinDbgAPI.getDump(request.getDumpPath());
        } catch (IOException e) {
            log.error("Encountered an error while trying to process Dump: " + request.getDumpPath(), e);
            throw e;
        }
        log.info("Finished processing Dump: " + request.getDumpPath());
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(file, dump);
        } catch (IOException e) {
            log.error("Encountered an error while trying to parse Dump: " + request.getDumpPath() + " into JSON: " + request.getJsonPath(), e);
            throw e;
        }
        log.info("Finished parsing Dump: " + request.getDumpPath() + " into JSON: " + request.getJsonPath());
        request.notifyObservers(dump);
    }

    /**
     * Converting dump files into json files according to requests.
     * @param requests List of DumpToJsonRequests.
     * @param parallel boolean representing whether or not to parallel the process.
     * @throws InterruptedException if performed parallel and was interrupted.
     */
    public void dumpsToJsons(List<DumpToJsonRequest> requests, boolean parallel) throws InterruptedException {
        log.info("Trying to parse " + requests.size() + " Dumps into JSONs");
        int count = 0;
        if (parallel) {
            ExecutorService executor = Executors.newFixedThreadPool(8);
            for (DumpToJsonRequest request : requests) {
                executor.submit(new RunnableJsonDumpWriter(request));
            }
            executor.shutdown();
            try {
                executor.awaitTermination(requests.size() * DUMP_PROCESS_TIME, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                log.error("Interrupted while parsing Dumps into JSONs", e);
                throw e;
            }
        } else {
            for (DumpToJsonRequest request : requests) {
                try {
                    dumpToJson(request);
                    count++;
                } catch (IOException e) {
                    log.warn("Failed to parse Dump " + request.getDumpPath() + " into JSON. Will continue with other Dumps");
                }
            }
        }
        log.info("Finished parsing " + ((count > 0) ? count + " " : "") + "Dumps into JSONs");
    }

    private class RunnableJsonDumpWriter implements Runnable{

        private final DumpToJsonRequest request;

        private RunnableJsonDumpWriter(DumpToJsonRequest request) {
            this.request = request;
        }

        @Override
        public void run() {
            try {
                dumpToJson(request);
            } catch (IOException e) {
                log.warn("Failed to parse Dump " + request.getDumpPath() + " into JSON. Will continue with other Dumps");
            }
        }
    }

}
