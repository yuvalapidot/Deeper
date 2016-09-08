package extractor;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * A simple extractor that always returns null but wait untill the output is finished.
 */
public class VoidWinDbgExtractor extends AbstractWinDbgExtractor {

    @Override
    public Object extract(BufferedReader reader) throws IOException {
        log.debug("Reading output");
        String line;
        while ((line = readLine(reader)) != null) {
            log.trace(line);
        }
        return null;
    }
}
