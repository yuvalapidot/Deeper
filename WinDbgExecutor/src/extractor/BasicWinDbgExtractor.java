package extractor;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * An extractor that simply returns the output String.
 */
public class BasicWinDbgExtractor extends AbstractWinDbgExtractor<String> {

    @Override
    public String extract(BufferedReader reader) throws IOException {
        log.debug("Returning output string");
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = readLine(reader)) != null) {
            log.trace(line);
            builder.append(line);
            builder.append("\n");
        }
        return builder.toString();
    }
}
