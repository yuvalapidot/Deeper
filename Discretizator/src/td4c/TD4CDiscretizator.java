package td4c;

import model.data.DataTable;
import model.feature.CsvNumberRepresentation;
import model.feature.DiscreteFeature;
import model.feature.Feature;
import model.instance.Instance;
import model.instance.InstanceSetType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import td4c.measures.IDistanceMeasure;

import java.util.*;

public class TD4CDiscretizator {

    private Map<String, Set<Instance>> classedInstances;
    private IDistanceMeasure distanceMeasure;

    private static final Logger log = LogManager.getLogger(TD4CDiscretizator.class);

    public TD4CDiscretizator(Set<Instance> instances, IDistanceMeasure distanceMeasure) {
        this.distanceMeasure = distanceMeasure;
        this.classedInstances = new LinkedHashMap<>();
        for (Instance instance : instances) {
            Set<Instance> classSet = classedInstances.get(instance.getClassification());
            if (classSet == null) {
                classSet = new LinkedHashSet<>();
                classedInstances.put(instance.getClassification(), classSet);
            }
            classSet.add(instance);
        }
    }

    public DataTable discrete(DataTable table, int bins, double threshold) {
        DataTable discreteTable = new DataTable();
        List<DiscreteFeature> discreteFeatures = new ArrayList<>();
        Feature classFeature = null;
        int discreteFeatureCount = 0;
        for (Feature feature : table.getFeatures()) {
            if (feature.getKey().equals("Class")) {
                classFeature = feature;
            } else {
                DiscreteFeature discreteFeature = discrete(feature, bins);
                if (discreteFeature.getRank() >= threshold) {
//                    discreteFeature.setDataTable(discreteTable);
                    discreteFeatures.add(discreteFeature);
                    discreteFeatureCount++;
                }
            }
        }
        log.info(discreteFeatureCount + " features out of " + table.getFeatures().size() + " were discrete and ranked above threshold (" + threshold + ")");
        discreteFeatures.sort(DiscreteFeature::compareTo);
        Collections.reverse(discreteFeatures);
        int numberOfInstances = table.getInstances().size();
        int addedFeatureCount = 0;
        for (DiscreteFeature discreteFeature : discreteFeatures) {
            boolean toAdd = true;
            for (Feature existingFeature : discreteTable.getFeatures()) {
                if (existingFeature.correlationRatio(discreteFeature, CsvNumberRepresentation.Integer_Representation) > ((numberOfInstances - 1) / (double) numberOfInstances)) {
                    toAdd = false;
                    break;
                }
            }
            if (toAdd) {
                discreteTable.put(discreteFeature);
                addedFeatureCount++;
            }
        }
        log.info(addedFeatureCount + " Non correlated features out of " + discreteFeatureCount + " discrete and ranked features were added to discrete data table");
        discreteTable.put(classFeature);
        return discreteTable;
    }

    public DiscreteFeature discrete(Feature<Integer> feature, int bins) {
        List<Integer> optionalCutoffs = getOptionalCutoffs(feature);
        List<Integer> currentCutoffs = new ArrayList<>();
        for (int i = 1; i < bins && optionalCutoffs.size() > 0; i++) {
            Integer bestCutoff = null;
            double bestCutoffDistance = 0;
            for (Integer possibleCutoff : optionalCutoffs) {
                List<Integer> tempCutoffs = new ArrayList<>(currentCutoffs);
                tempCutoffs.add(possibleCutoff);
                double cutoffDistance = evaluateCutoffs(feature, tempCutoffs);
                if (cutoffDistance >= bestCutoffDistance) {
                    bestCutoffDistance = cutoffDistance;
                    bestCutoff = possibleCutoff;
                }
            }
            currentCutoffs.add(bestCutoff);
            optionalCutoffs.remove(bestCutoff);
            currentCutoffs.sort(Integer::compareTo);
        }
        return createDiscreteFeature(feature, currentCutoffs);
    }

    private DiscreteFeature createDiscreteFeature(Feature<Integer> feature, List<Integer> cutoffs) {
        DiscreteFeature discreteFeature = new DiscreteFeature(feature.getKey(), 0, null);
        discreteFeature.setCutoffs(cutoffs);
        discreteFeature.setRank(evaluateCutoffs(feature, cutoffs));
        for (Instance instance : feature.getAllConcritInstances()) {
            Integer value = feature.getValue(instance);
            int discreteIndex = 0;
            for (Integer cutoff : cutoffs) {
                if (value < cutoff) {
                    break;
                }
                discreteIndex++;
            }
            discreteFeature.setValue(instance, discreteIndex);
        }
        return discreteFeature;
    }

    private double evaluateCutoffs(Feature<? extends Integer> feature, List<Integer> cutoffs) {
        cutoffs.sort(Integer::compareTo);
        int[][] bins = getBins(feature, cutoffs);
        return distanceMeasure.cutoffScore(classedInstances.size(), bins);
    }

    private int[][] getBins(Feature<? extends Integer> feature, List<Integer> sortedCutoffs) {
        int[][] bins = new int[classedInstances.size()][sortedCutoffs.size() + 1];
        int classIndex = 0;
        for (String className : classedInstances.keySet()) {
            for (Instance instance : classedInstances.get(className)) {
                if (instance.getSetType().equals(InstanceSetType.TRAIN_SET)) {
                    int cutoffIndex = 0;
                    for (Integer cutoff : sortedCutoffs) {
                        if (feature.getValue(instance) < cutoff) {
                            break;
                        }
                        cutoffIndex++;
                    }
                    bins[classIndex][cutoffIndex]++;
                }
            }
            classIndex++;
        }
        return bins;
    }

    private List<Integer> getOptionalCutoffs(Feature<Integer> feature) {
        List<Integer> optionalCutoffs = new ArrayList<>();
        for (int featureValue : feature.getAllValues()) {
            optionalCutoffs.add(featureValue);
        }
        optionalCutoffs.sort(Integer::compareTo);
        return optionalCutoffs;
    }

    public String getType() {
        return distanceMeasure.getName();
    }
}
