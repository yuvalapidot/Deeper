package executor;

import command.ICommand;
import extractor.IExtractor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public abstract class AbstractExecutor {

    protected Process process;
    protected final Logger log = LogManager.getLogger(getClass());

    public void execute(ICommand command) throws IOException {
        log.info("Executing command - " + command.getCommand());
        process = Runtime.getRuntime().exec(command.getCommand());
    }

    public abstract <T> T getOutput(IExtractor<T> extractor) throws IOException;

    public int waitFor() {
        log.info("Waiting for process to exit");
        try {
            return process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
