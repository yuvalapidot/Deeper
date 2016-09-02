package executor;

import extractor.IExtractor;

import java.io.IOException;

public class NonInteractiveExecutor extends AbstractExecutor {

    @Override
    public <T> T getOutput(IExtractor<T> extractor) throws IOException {
        log.info("Trying to get output");
        try {
            process.waitFor();
            return extractor.extract(process.getInputStream());
        } catch (InterruptedException e) {
            log.error("encountered an error while waiting for process to exit", e);
            return null;
        }
    }
}
