package extractor;

import api.WinDbgAPI;
import model.memory.Dump;
import model.memory.Process;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class WinDbgExtractor {

    //    public Collection<Dump> getDumpsFromDirectory(String directoryPath) throws Exception {
//        return getDumpsFromDirectory(directoryPath, DumpClassification.Unknown);
//    }

    public List<Dump> getDumpsFromDirectory(String directoryPath) throws Exception {
        Collection<String> files = getDumpsNamesFromDirectory(directoryPath);
//        List<Future<Dump>> futures = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(8);
//        for (String dumpFile : files) {
//            DumpCreator dumpCreator = new DumpCreator(dumpFile);
//            Future<Dump> future = executor.submit(dumpCreator);
//            futures.add(future);
//        }
        List<Callable<Dump>> dumpCreators = new ArrayList<>();
        for (String dumpFile : files) {
            dumpCreators.add(new DumpCreator(dumpFile));
        }
        List<Future<Dump>> futures = executor.invokeAll(dumpCreators);
        executor.shutdown();
//        executor.awaitTermination(2, TimeUnit.HOURS);
        List<Dump> dumps = new ArrayList<>();
        for (Future<Dump> future : futures) {
            Dump dump = future.get();
            if (dump.getName().contains("Benign")) {
                dump.setClassification("Benign");
            } else if (dump.getName().contains("Malicious")) {
                dump.setClassification("Malicious");
            }
            dumps.add(dump);
        }
        return dumps;
    }

    private Collection<String> getDumpsNamesFromDirectory(String path) throws IOException {
        Collection<String> files = new ArrayList<>();
        java.nio.file.Files.walk(Paths.get(path)).filter(filePath -> Files.isRegularFile(filePath)).forEach(filePath -> {
            files.add(filePath.toString());
        });
        files.remove(path);
        return files;
    }

    private class DumpCreator implements Callable<Dump> {

        private final String dumpFilePath;

        DumpCreator(String dumpFilePath) {
            this.dumpFilePath = dumpFilePath;
        }

        @Override
        public Dump call() throws Exception {
            Dump dump = new Dump(dumpFilePath);
            List<Process> processes = WinDbgAPI.getProcessesFromDump(dumpFilePath);
            dump.addProcesses(processes);
            return dump;
        }
    }
}
