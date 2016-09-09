package writer;

import model.data.DataTable;
import model.feature.Feature;
import model.feature.FeatureValue;
import model.feature.Instance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;

public class DataTableCsvWriter {

    private final Logger log = LogManager.getLogger(DataTableCsvWriter.class);

    private static final String INSTANCES = "Instance";
    private static final char CSV_DELIMITER = ',';
    private static final char CSV_NON_DELIMITER = '|';
    private static final char CSV_NEW_LINE = '\n';

    public void dataTableToCsv(DataTableToCsvRequest request) throws IOException {
        log.info("Trying to write Data Table into csv: " + request.getCsvPath());
        try (FileWriter writer = new FileWriter(request.getCsvPath())){
            writer.write(dataTableToCsvString(request.getDataTable()));
        } catch (IOException e) {
            log.info("Encountered an error while writing Data Table into csv: " + request.getCsvPath(), e);
            throw e;
        }
        log.info("Finished writing Data Table into csv: " + request.getCsvPath());
        request.notifyObservers();
    }

    private String dataTableToCsvString(DataTable table) {
        log.debug("Converting Data Table into csv String. Using '" + CSV_DELIMITER
                + "' as Delimiter. Replacing all former occurrences of '" + CSV_DELIMITER
                + "' with '" + CSV_NON_DELIMITER + "'.");
        StringBuilder builder = new StringBuilder(INSTANCES);
        for (Feature feature : table.getFeatures()) {
            builder.append(CSV_DELIMITER);
            builder.append(csvString(feature.getKey()));
        }
        for (Instance instance : table.getInstances()) {
            builder.append(CSV_NEW_LINE);
            builder.append(csvString(instance));
            for (Feature feature : table.getFeatures()) {
                builder.append(CSV_DELIMITER);
                FeatureValue value = feature.getValue(instance);
                if (value.getValue() != null) {
                    builder.append(csvString(value));
                }
            }
        }
        return builder.toString();
    }

    private String csvString(Object o) {
        return o.toString().replace(CSV_DELIMITER, CSV_NON_DELIMITER);
    }
}
