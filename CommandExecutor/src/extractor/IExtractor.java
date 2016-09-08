package extractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface IExtractor <T> {

    /**
     * Extract T from String input.
     * @param input String.
     * @return T extracted from input.
     */
    T extract(String input);

    /**
     * Extract T from input stream.
     * @param input InputStream.
     * @return T extracted from input.
     * @throws IOException
     */
    T extract(InputStream input) throws IOException;

    /**
     * Extract T from input file.
     * @param file File
     * @return T extracted from input.
     * @throws IOException
     */
    T extract(File file) throws IOException;

    /**
     * Extract T from input buffered reader.
     * @param reader BufferedReader
     * @return T extracted from input.
     * @throws IOException
     */
    T extract(BufferedReader reader) throws IOException;
}
