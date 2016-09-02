package extractor;

import memory.model.Thread;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ThreadExtractor extends AbstractExtractor <List<Thread>> {

    @Override
    public List<Thread> extract(BufferedReader reader) throws IOException {
        log.debug("Extracting threads from output");
        List<Thread> threads = new ArrayList<>();
        String line;
        Thread currentThread = null;
        while ((line = readLine(reader)) != null) {
            log.trace(line);
            if (line.isEmpty()) {
                currentThread = null;
                continue;
            }
            String[] chunks = line.split(" ");
            if (currentThread == null) {
                if (chunks[0].equals("THREAD")) {
                    currentThread = new Thread(chunks[1]);
                    threads.add(currentThread);
                } else {
                    continue;
                }
            }
            editThread(currentThread, chunks);
        }
        return threads;
    }

    private void editThread(Thread thread, String[] chunks) {
        List<String> chunkList = Arrays.asList(chunks);
        int index;
        if ((index = chunkList.indexOf("Cid")) >= 0) {
            thread.setCid(chunkList.get(index + 1));
        }
        if ((index = chunkList.indexOf("Teb:")) >= 0) {
            thread.setTeb(chunkList.get(index + 1));
        }
        if ((index = chunkList.indexOf("Win32Thread:")) >= 0) {
            thread.setWin32Thread(chunkList.get(index + 1));
        }
    }
}
