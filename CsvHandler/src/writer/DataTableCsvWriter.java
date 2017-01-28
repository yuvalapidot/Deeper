package writer;

import model.data.DataTable;
import model.feature.DiscreteFeature;
import model.feature.Feature;
import model.instance.Instance;
import model.instance.InstanceSetType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
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
            writer.write(dataTableToCsvString(request.getDataTable(), request.getRepresentation(), request.getInstanceSetTypesFilter(), request.getFeaturePercentage(), request.getScoreThreshold()));
        } catch (IOException e) {
            log.info("Encountered an error while writing Data Table into csv: " + request.getCsvPath(), e);
            throw e;
        }
        log.info("Finished writing Data Table into csv: " + request.getCsvPath());
        request.notifyObservers();
    }

    public void dataTablesToCsv(DataTablesToCsvRequest request) throws IOException {
        log.info("Trying to write Data Table into csv: " + request.getCsvPath());
        File csv = new File(request.getCsvPath());
        csv.getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(csv)){
            writeTablesToCsv(writer, request.getDataTable(), request.getRepresentation(), request.getInstanceSetTypesFilter());
        } catch (IOException e) {
            log.info("Encountered an error while writing Data Table into csv: " + request.getCsvPath(), e);
            throw e;
        }
        log.info("Finished writing Data Table into csv: " + request.getCsvPath());
        request.notifyObservers();
    }

    private String dataTableToCsvString(DataTable table, CsvNumberRepresentation representation, Set<InstanceSetType> instanceSetTypesFilter, int featurePercentage, double featureThreshold) {
        log.debug("Converting Data Table into csv String. Using '" + CSV_DELIMITER
                + "' as Delimiter. Replacing all former occurrences of '" + CSV_DELIMITER
                + "' with '" + CSV_NON_DELIMITER + "'.");
//        StringBuilder builder = new StringBuilder(INSTANCES + CSV_DELIMITER);
        StringBuilder builder = new StringBuilder();
        Set<Feature> features = table.getFeatures();
        boolean addSeparator = false;
        int requiredFeaturesCount = featurePercentage * features.size() / 100;
        int featureCounter = 0;
        for (Feature feature : features) {
            if (addSeparator) {
                builder.append(CSV_DELIMITER);
            } else {
                addSeparator = true;
            }
            boolean shouldBreak = false;
            if (feature instanceof DiscreteFeature) {
                DiscreteFeature dFeature = (DiscreteFeature) feature;
                if (dFeature.getRank() < featureThreshold) {
                    shouldBreak = true;
                }
            }
            if (featureCounter >= requiredFeaturesCount) {
                shouldBreak = true;
            }
            if (shouldBreak) {
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
//                String[] splitedName = instance.getName().split("\\\\");
//                builder.append(csvString(splitedName[splitedName.length - 1].replace("MalSnap-IIS_sn_", "").replace(".json", "")) + CSV_DELIMITER);
                addSeparator = false;
                featureCounter = 0;
                for (Feature feature : features) {
                    boolean shouldBreak = false;
                    if (feature instanceof DiscreteFeature) {
                        DiscreteFeature dFeature = (DiscreteFeature) feature;
                        if (dFeature.getRank() < featureThreshold) {
                            shouldBreak = true;
                        }
                    }
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

    private void writeTablesToCsv(FileWriter writer, DataTable[] tables, CsvNumberRepresentation[] representations, Set<InstanceSetType> instanceSetTypesFilter) throws IOException {
        log.debug("Converting Data Tables into csv String. Using '" + CSV_DELIMITER
                + "' as Delimiter. Replacing all former occurrences of '" + CSV_DELIMITER
                + "' with '" + CSV_NON_DELIMITER + "'.");
//        StringBuilder builder = new StringBuilder(INSTANCES + CSV_DELIMITER);
        StringBuilder builder = new StringBuilder();
        Set<Feature> features = new LinkedHashSet<>();
        for (DataTable table : tables) {
            features.addAll(table.getFeatures());
        }
        List<Feature> classFeatures = new ArrayList<>();
        for (Feature feature : features) {
            if (feature.getKey().equals("Class")) {
                classFeatures.add(feature);
            }
        }
        for (int i = 0; i < classFeatures.size() - 1; i++) {
            features.remove(classFeatures.get(i));
        }
        boolean addSeparator = false;
        for (Feature feature : features) {
            for (CsvNumberRepresentation representation : representations) {
                if (addSeparator) {
                    builder.append(CSV_DELIMITER);
                } else {
                    addSeparator = true;
                }
                if (feature.getKey().equals("Class")) {
                    builder.append(csvString(feature.getKey()));
                    break;
                }
                builder.append(csvString(representation.toString() + "_" + feature.getKey()));
            }
        }
        writer.write(builder.toString());
        builder = new StringBuilder();
        for (InstanceSetType type : instanceSetTypesFilter) {
            for (Instance instance : tables[0].getInstances()) {
                if (instance.getSetType() != type) {
                    continue;
                }
                builder.append(CSV_NEW_LINE);
//                builder.append(csvString(instance.getName()) + CSV_DELIMITER);
                addSeparator = false;
                for (Feature feature : features) {
                    for (CsvNumberRepresentation representation : representations) {
                        if (addSeparator) {
                            builder.append(CSV_DELIMITER);
                        } else {
                            addSeparator = true;
                        }
                        Object value =  getValue(instance, feature, representation);
                        if (value != null) {
                            builder.append(csvString(value));
                        }
                        if (feature.getKey().equals("Class")) {
                            break;
                        }
                    }
                }
                writer.write(builder.toString());
                builder = new StringBuilder();
            }
        }
    }

    private Object getValue(Instance instance, Feature feature, CsvNumberRepresentation representation) {
        DataTable table = feature.getDataTable();
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
