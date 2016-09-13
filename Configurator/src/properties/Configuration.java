package properties;

import exception.ConfigurationFileReadingException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuration {

    private static String configFilePath = "resources/config.properties";
    private static volatile Properties properties = null;
    private static final Object lock = new Object();

    private static void initialize() {
        FileInputStream input = null;
        try {
            input = new FileInputStream(configFilePath);
            properties = new Properties();
            properties.load(input);
        } catch (Exception ex) {
            throw new ConfigurationFileReadingException("Encountered a fatal problem while trying to read configuration file - " + configFilePath, ex);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException ioe) {
                    throw new ConfigurationFileReadingException("Encountered a fatal problem while trying to close configuration file - " + configFilePath, ioe);
                }
            }
        }
    }

    public static Object get(String property) {
        if (properties == null) {
            synchronized (lock){
                if (properties == null) {
                    initialize();
                }
            }
        }
        return properties.getProperty(property);
    }

    public static int getInt(String property) {
        return Integer.valueOf(getString(property));
    }

    public static double getDouble(String property) {
        return Double.valueOf(getString(property));
    }

    public static String getString(String property) {
        return (String) get(property);
    }
}
