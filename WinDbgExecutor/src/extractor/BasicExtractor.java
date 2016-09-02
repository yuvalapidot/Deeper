package extractor;

import java.io.BufferedReader;
import java.io.IOException;

public class BasicExtractor extends AbstractExtractor <String> {

    @Override
    public String extract(BufferedReader reader) throws IOException {
        log.info("Returning output");
        StringBuilder builder = new StringBuilder(readLine(reader));
        String line;
        while ((line = readLine(reader)) != null) {
            log.trace(line);
            builder.append(line);
            builder.append("\n");
        }
        return builder.toString();
    }
}
