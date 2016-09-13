package executor;

import command.ICommand;
import extractor.IExtractor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

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
            log.error("Encountered an error while waiting for process to exit", e);
            return -1;
        }
    }

    public boolean waitFor(long timeout, TimeUnit unit) {
        log.info("Waiting for process to exit");
        try {
            return process.waitFor(timeout, unit);
        } catch (InterruptedException e) {
            log.error("Encountered an error while waiting for process to exit", e);
            return false;
        }
    }
}
