package extractor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public abstract class AbstractWinDbgExtractor<T> implements IExtractor<T> {

    protected final Logger log = LogManager.getLogger(getClass());

    private static String regexString = "([0-9]*: )?kd> ";
    private static int kdLineLength = 4;

    public T extract(String input) {
        BufferedReader bufferedReader = new BufferedReader(new StringReader(input));
        try {
            return extract(bufferedReader);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public T extract(InputStream input) throws IOException {
        InputStreamReader streamReader = new InputStreamReader(input);
        BufferedReader bufferedReader = new BufferedReader(streamReader);
        T output = extract(bufferedReader);
        bufferedReader.close();
        streamReader.close();
        return output;
    }

    public T extract(File file) throws IOException {
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        T output = extract(bufferedReader);
        bufferedReader.close();
        fileReader.close();
        return output;
    }

    public abstract T extract(BufferedReader reader) throws IOException;

    protected String readLine(BufferedReader reader) throws IOException {
        log.trace("Reading line from buffered reader");
        int c;
        StringBuilder builder = new StringBuilder();
        try {
            while ((c = reader.read()) != -1) {
                char ch = (char) c;
                if (endOfLine(ch)) {
                    log.trace("Reached end of line");
                    return builder.toString().trim();
                }
                builder.append(ch);
                if (builder.length() >= kdLineLength) {
                    if (builder.toString().matches(regexString)) {
                        return null;
                    }
                }
            }
        } catch (IOException e) {
            log.error("Encountered an error while reading character from buffered reader", e);
            throw e;
        }
        log.trace("Reached end of line");
        if (builder.length() > 0) {
            return builder.toString().trim();
        }
        log.trace("line was empty");
        return null;
    }

    private boolean endOfLine(char c) {
        return c == '\n' || c == '\r';
    }
}
