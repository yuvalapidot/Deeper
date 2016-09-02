package extractor;

import java.io.BufferedReader;
import java.io.IOException;

public class VoidExtractor extends AbstractExtractor {

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
