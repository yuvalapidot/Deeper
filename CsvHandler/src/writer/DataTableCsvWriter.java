package writer;

import model.data.DataTable;
import model.feature.CsvNumberRepresentation;
import model.feature.Feature;
import model.instance.Instance;
import model.instance.InstanceSetType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

public class DataTableCsvWriter {

    private final Logger log = LogManager.getLogger(DataTableCsvWriter.class);

    private static final String INSTANCES = "Time";
    private static final char CSV_DELIMITER = ',';
    private static final char CSV_NON_DELIMITER = '|';
    private static final char CSV_NEW_LINE = '\n';

    public void dataTableToCsv(DataTableToCsvRequest request) throws IOException {
        log.info("Trying to write Data Table into csv: " + request.getCsvPath());
        File csv = new File(request.getCsvPath());
        csv.getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(csv)){
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
        StringBuilder builder = new StringBuilder();
        Set<Feature> features = table.getFeatures();
        boolean addSeparator = false;
        int requiredFeaturesCount = features.size();
        int featureCounter = 0;
        for (Feature feature : features) {
            if (addSeparator) {
                builder.append(CSV_DELIMITER);
            } else {
                addSeparator = true;
            }
            if (featureCounter >= requiredFeaturesCount) {
                builder.append(csvString("Class"));
                break;
            }
            builder.append(csvString(feature.getKey()));
            featureCounter++;
        }
        for (InstanceSetType type : instanceSetTypesFilter) {
            for (Instance instance : table.getInstances()) {
                if (instance.getSetType() != type) {
                    continue;
                }
                builder.append(CSV_NEW_LINE);
                addSeparator = false;
                featureCounter = 0;
                for (Feature feature : features) {
                    boolean shouldBreak = false;
                    if (featureCounter >= requiredFeaturesCount) {
                        shouldBreak = true;
                    }
                    if (shouldBreak) {
                        feature = table.getFeature("Class");
                    }
                    if (addSeparator) {
                        builder.append(CSV_DELIMITER);
                    } else {
                        addSeparator = true;
                    }
                    Object value =  getValue(instance, feature, representation);
                    if (value != null) {
                        builder.append(csvString(value));
                    }
                    if (shouldBreak) {
                        break;
                    }
                    featureCounter++;
                }
            }
        }
        return builder.toString();
    }

    private Object getValue(Instance instance, Feature feature, CsvNumberRepresentation representation) {
        DataTable table = feature.getDataTable();
        Object value = feature.getValue(instance);
        if (!(value instanceof Integer)) {
            return value;
        }
        if (representation.equals(CsvNumberRepresentation.Integer_Representation)) {
            return value;
        } else if (representation.equals(CsvNumberRepresentation.Binary_Representation)) {
            return feature.getBinaryValue(instance);
        } else if (representation.equals(CsvNumberRepresentation.TF_Representation)) {
            return table.getTimeFrequencyValue(instance, feature);
        } else if (representation.equals(CsvNumberRepresentation.TFIDF_Representation)) {
            return table.getTimeFrequencyInverseDocumentFrequencyValue(instance, feature);
        } else {
            return value;
        }
    }

    private String csvString(Object o) {
        String csvString = o.toString().replace(CSV_DELIMITER, CSV_NON_DELIMITER);
        return "\"" + csvString + "\"";
    }
}
