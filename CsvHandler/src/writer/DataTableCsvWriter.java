package writer;

import model.data.DataTable;
import model.feature.Feature;
import model.feature.FeatureValue;
import model.instance.Instance;
import model.instance.InstanceSetType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

public class DataTableCsvWriter {

    private final Logger log = LogManager.getLogger(DataTableCsvWriter.class);

    private static final String INSTANCES = "Instance";
    private static final char CSV_DELIMITER = ',';
    private static final char CSV_NON_DELIMITER = '|';
    private static final char CSV_NEW_LINE = '\n';

    public void dataTableToCsv(DataTableToCsvRequest request) throws IOException {
        log.info("Trying to write Data Table into csv: " + request.getCsvPath());
        try (FileWriter writer = new FileWriter(request.getCsvPath())){
            writer.write(dataTableToCsvString(request.getDataTable(), request.getRepresentation(), request.getInstanceSetTypesFilter()));
        } catch (IOException e) {
            log.info("Encountered an error while writing Data Table into csv: " + request.getCsvPath(), e);
            throw e;
        }
        log.info("Finished writing Data Table into csv: " + request.getCsvPath());
        request.notifyObservers();
    }

    private String dataTableToCsvString(DataTable table, CsvNumberRepresentation representation, Set<InstanceSetType> instanceSetTypesFilter) {
        log.debug("Converting Data Table into csv String. Using '" + CSV_DELIMITER
                + "' as Delimiter. Replacing all former occurrences of '" + CSV_DELIMITER
                + "' with '" + CSV_NON_DELIMITER + "'.");
        StringBuilder builder = new StringBuilder(INSTANCES);
        Set<Feature> features = table.getFeatures();

        for (Feature feature : features) {
            builder.append(CSV_DELIMITER);
            builder.append(csvString(feature.getKey()));
        }
        for (Instance instance : table.getInstances()) {
            if (!instanceSetTypesFilter.contains(instance.getSetType())) {
                continue;
            }
            builder.append(CSV_NEW_LINE);
            builder.append(csvString(instance.getName()));
            for (Feature feature : features) {
                builder.append(CSV_DELIMITER);
                FeatureValue value =  getValue(table, instance, feature, representation);
                if (value.getValue() != null) {
                    builder.append(csvString(value));
                }
            }
        }
        return builder.toString();
    }

    private FeatureValue getValue(DataTable table, Instance instance, Feature feature, CsvNumberRepresentation representation) {
        if (representation.equals(CsvNumberRepresentation.INTEGER_REPRESENTATION)) {
            return feature.getValue(instance);
        } else if (representation.equals(CsvNumberRepresentation.BINARY_REPRESENTATION)) {
            return feature.getBinaryValue(instance);
        } else if (representation.equals(CsvNumberRepresentation.TF_REPRESENTATION)) {
            return table.getTimeFrequencyValue(instance, feature);
        } else if (representation.equals(CsvNumberRepresentation.TFIDF_REPRESENTATION)) {
            return table.getTimeFrequencyInverseDocumentFrequencyValue(instance, feature);
        } else {
            return feature.getValue(instance);
        }
    }

    private String csvString(Object o) {
        String csvString = o.toString().replace(CSV_DELIMITER, CSV_NON_DELIMITER);
        return "\"" + csvString + "\"";
    }
}
