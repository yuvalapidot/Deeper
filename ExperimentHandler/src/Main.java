import model.data.DataTable;
import model.feature.Feature;
import model.feature.FeatureKey;
import model.instance.Instance;
import model.instance.StringInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import reader.CsvToDataTableRequest;
import reader.DataTableCsvReader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Main {

    private static final Logger log = LogManager.getLogger(Main.class);

    private static final String experimentRootFolder = "C:\\Users\\yuval\\Dropbox\\NGrams\\Results 50";
    private static final String resultFilePath = "C:\\Users\\yuval\\Dropbox\\NGrams\\Results 50\\Results Combined.csv";

    private static final FeatureKey<String, String> keySchemeFeatureKey = new FeatureKey<>("Key_Scheme");
    private static final FeatureKey<String, String> keyDatasetFeatureKey = new FeatureKey<>("Key_Dataset");
    private static final FeatureKey<String, String> truePositiveFeatureKey = new FeatureKey<>("True_positive_rate");
    private static final FeatureKey<String, String> falsePositiveFeatureKey = new FeatureKey<>("False_positive_rate");
    private static final FeatureKey<String, String> trueNegativeFeatureKey = new FeatureKey<>("True_negative_rate");
    private static final FeatureKey<String, String> falseNegativeFeatureKey = new FeatureKey<>("False_negative_rate");
    private static final FeatureKey<String, String> fMeasureFeatureKey = new FeatureKey<>("F_measure");
    private static final FeatureKey<String, String> aucFeatureKey = new FeatureKey<>("Area_under_ROC");

    public static void main(String[] args) throws IOException {
        DataTableCsvReader reader = new DataTableCsvReader();
        Map<String, Map<String, Map<String, Map<String, Map<String, List<Double>>>>>> experimentsMap = new LinkedHashMap<>();
        for (File file : getExperimentFiles()) {
            CsvToDataTableRequest request = new CsvToDataTableRequest(file.getPath(), false);
            DataTable table = reader.csvToDataTable(request);
            Feature<String> keyScheme = table.getFeature(keySchemeFeatureKey);
            Feature<String> keyDataset = table.getFeature(keyDatasetFeatureKey);
            Feature<String> truePositive = table.getFeature(truePositiveFeatureKey);
            Feature<String> falsePositive = table.getFeature(falsePositiveFeatureKey);
            Feature<String> trueNegative = table.getFeature(trueNegativeFeatureKey);
            Feature<String> falseNegative = table.getFeature(falseNegativeFeatureKey);
            Feature<String> fMeasure = table.getFeature(fMeasureFeatureKey);
            Feature<String> auc = table.getFeature(aucFeatureKey);
            String experiment = file.getName().replace("Experiment ", "").replace(" Results.csv", "");
            Map<String, Map<String, Map<String, Map<String, List<Double>>>>> experimentMap = new LinkedHashMap<>();
            experimentsMap.put(experiment, experimentMap);
            for (Instance instance : table.getInstances()) {
                String classifier = keyScheme.getValue(instance).getValue();
                classifier = classifier.substring(classifier.lastIndexOf('.') + 1);
                Map<String, Map<String, Map<String, List<Double>>>> classifierMap = experimentMap.get(classifier);
                if (classifierMap == null) {
                    classifierMap = new LinkedHashMap<>();
                    experimentMap.put(classifier, classifierMap);
                }
                String dataset = keyDataset.getValue(instance).getValue();
                String featureRepresentation = dataset.substring(dataset.indexOf('-') + 1, dataset.indexOf("-train"));
                Map<String, Map<String, List<Double>>> featureRepresentationMap = classifierMap.get(featureRepresentation);
                if (featureRepresentationMap == null) {
                    featureRepresentationMap = new LinkedHashMap<>();
                    classifierMap.put(featureRepresentation, featureRepresentationMap);
                }
                String n = dataset.substring(0, dataset.indexOf('-'));
                Map<String, List<Double>> nMap = featureRepresentationMap.get(n);
                if (nMap == null) {
                    nMap = new LinkedHashMap<>();
                    featureRepresentationMap.put(n, nMap);
                }
                List<Double> truePositiveList = nMap.get("TP");
                if (truePositiveList == null) {
                    truePositiveList = new ArrayList<>();
                    nMap.put("TP", truePositiveList);
                }
                truePositiveList.add(Double.parseDouble(truePositive.getValue(instance).getValue()));
                List<Double> falsePositiveList = nMap.get("FP");
                if (falsePositiveList == null) {
                    falsePositiveList = new ArrayList<>();
                    nMap.put("FP", falsePositiveList);
                }
                falsePositiveList.add(Double.parseDouble(falsePositive.getValue(instance).getValue()));
                List<Double> trueNegativeList = nMap.get("TN");
                if (trueNegativeList == null) {
                    trueNegativeList = new ArrayList<>();
                    nMap.put("TN", trueNegativeList);
                }
                trueNegativeList.add(Double.parseDouble(trueNegative.getValue(instance).getValue()));
                List<Double> falseNegativeList = nMap.get("FN");
                if (falseNegativeList == null) {
                    falseNegativeList = new ArrayList<>();
                    nMap.put("FN", falseNegativeList);
                }
                falseNegativeList.add(Double.parseDouble(falseNegative.getValue(instance).getValue()));
                List<Double> fMeasureList = nMap.get("F-Measure");
                if (fMeasureList == null) {
                    fMeasureList = new ArrayList<>();
                    nMap.put("F-Measure", fMeasureList);
                }
                fMeasureList.add(Double.parseDouble(fMeasure.getValue(instance).getValue()));
                List<Double> aucList = nMap.get("AUC");
                if (aucList == null) {
                    aucList = new ArrayList<>();
                    nMap.put("AUC", aucList);
                }
                aucList.add(Double.parseDouble(auc.getValue(instance).getValue()));
                log.info(classifier);
            }
        }
        printToCsv(resultFilePath, experimentCsvString(experimentsMap));
    }

    private static String experimentCsvString(Map<String, Map<String, Map<String, Map<String, Map<String, List<Double>>>>>> experimentsMap) {
        StringBuilder builder = new StringBuilder("Experiment,Classifier,Feature Representation,N,TP,FP,TN,FN,F-Measure,AUC\n");
        for (String experiemnt : experimentsMap.keySet()) {
            for (String classifier : experimentsMap.get(experiemnt).keySet()) {
                for (String featureRepresentation : experimentsMap.get(experiemnt).get(classifier).keySet()) {
                    for (String n : experimentsMap.get(experiemnt).get(classifier).get(featureRepresentation).keySet()) {
                        Map<String, List<Double>> values = experimentsMap.get(experiemnt).get(classifier).get(featureRepresentation).get(n);
                        builder.append(experiemnt).append(",").append(classifier).append(",").append(featureRepresentation).append(",").append(n);
                        builder.append(",").append(meanList(values.get("TP")));
                        builder.append(",").append(meanList(values.get("FP")));
                        builder.append(",").append(meanList(values.get("TN")));
                        builder.append(",").append(meanList(values.get("FN")));
                        builder.append(",").append(meanList(values.get("F-Measure")));
                        builder.append(",").append(meanList(values.get("AUC")));
                        builder.append("\n");
                    }
                }
            }
        }
        return builder.toString();
    }

//    private static String experimentBestCsvString(Map<String, Map<String, Map<String, Map<String, Map<String, List<Double>>>>>> experimentsMap) {
//        StringBuilder builder = new StringBuilder("Experiment,Classifier,Feature Representation,N,TP,FP,TN,FN,F-Measure,AUC\n");
//        for (String experiemnt : experimentsMap.keySet()) {
//            for (String classifier : experimentsMap.get(experiemnt).keySet()) {
//                List<String>
//                for (String featureRepresentation : experimentsMap.get(experiemnt).get(classifier).keySet()) {
//                    for (String n : experimentsMap.get(experiemnt).get(classifier).get(featureRepresentation).keySet()) {
//                        Map<String, List<Double>> values = experimentsMap.get(experiemnt).get(classifier).get(featureRepresentation).get(n);
//                        builder.append(experiemnt).append(",").append(classifier).append(",").append(featureRepresentation).append(",").append(n);
//                        builder.append(",").append(meanList(values.get("TP")));
//                        builder.append(",").append(meanList(values.get("FP")));
//                        builder.append(",").append(meanList(values.get("TN")));
//                        builder.append(",").append(meanList(values.get("FN")));
//                        builder.append(",").append(meanList(values.get("F-Measure")));
//                        builder.append(",").append(meanList(values.get("AUC")));
//                        builder.append("\n");
//                    }
//                }
//            }
//        }
//        return builder.toString();
//    }

    private static void printToCsv(String filePath, String csv) throws IOException {
        File file = new File(filePath);
        file.getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(filePath)){
            writer.write(csv);
        } catch (IOException e) {
            throw e;
        }
    }

    private static double meanList(List<Double> doubles) {
        double sum = 0;
        for (Double d : doubles) {
            sum += d;
        }
        return sum / doubles.size();
    }

    private static List<File> getExperimentFiles() throws IOException {
        List<File> files = new ArrayList<>();
        Files.walk(Paths.get(experimentRootFolder)).filter(filePath -> Files.isRegularFile(filePath) & filePath.toString().contains("Results.csv")).forEach(filePath -> files.add(filePath.toFile()));
        return files;
    }
}
