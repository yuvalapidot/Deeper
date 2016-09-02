package extractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by yuval on 9/3/2016.
 */
public interface IExtractor <T> {

    T extract(String input);

    T extract(InputStream input) throws IOException;

    T extract(File file) throws IOException;

    T extract(BufferedReader reader) throws IOException;
}
