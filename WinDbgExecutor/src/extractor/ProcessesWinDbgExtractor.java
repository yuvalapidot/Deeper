package extractor;

import model.memory.Process;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProcessesWinDbgExtractor extends AbstractWinDbgExtractor<List<Process>> {

    @Override
    public List<Process> extract(BufferedReader reader) throws IOException {
        log.debug("Extracting processes from output");
        List<Process> processes = new ArrayList<>();
        String line;
        Process currentProcess = null;
        while ((line = readLine(reader)) != null) {
            log.trace(line);
            if (line.isEmpty()) {
                currentProcess = null;
                continue;
            }
            String[] chunks = line.split(" ");
            if (currentProcess == null) {
                if (chunks[0].equals("PROCESS")) {
                    currentProcess = new Process(chunks[1]);
                    processes.add(currentProcess);
                } else {
                    continue;
                }
            }
            editProcess(currentProcess, chunks);
        }
        return processes;
    }

    private void editProcess(Process process, String[] chunks) {
        List<String> chunkList = Arrays.asList(chunks);
        int index;
        if ((index = chunkList.indexOf("SessionId:")) >= 0) {
            process.setSessionId(chunkList.get(index + 1));
        }
        if ((index = chunkList.indexOf("Cid:")) >= 0) {
            process.setCid(chunkList.get(index + 1));
        }
        if ((index = chunkList.indexOf("Peb:")) >= 0) {
            process.setPeb(chunkList.get(index + 1));
        }
        if ((index = chunkList.indexOf("ParentCid:")) >= 0) {
            process.setParentCid(chunkList.get(index + 1));
        }
        if ((index = chunkList.indexOf("Image:")) >= 0) {
            process.setImage(chunkList.get(index + 1));
        }
    }
}
