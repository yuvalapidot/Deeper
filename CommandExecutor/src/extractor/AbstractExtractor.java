package extractor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

/**
 * Created by yuval on 9/3/2016.
 */
public abstract class AbstractExtractor <T> implements IExtractor<T> {

    private static String regexString = "[0-9]*: kd> ";
    private static int kdLineLength = 7;
    protected final Logger log = LogManager.getLogger(getClass());

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
        int c;
        StringBuilder builder = new StringBuilder();
        while ((c = reader.read()) != -1) {
            char ch = (char) c;
            if (endOfLine(ch)) {
                return builder.toString().trim();
            }
            builder.append(ch);
            if (builder.length() >= kdLineLength) {
                if (builder.toString().matches(regexString)) {
                    return null;
                }
            }
        }
        if (builder.length() > 0) {
            return builder.toString().trim();
        }
        return null;
    }

    private boolean endOfLine(char c) {
        return c == '\n' || c == '\r';
    }
}
