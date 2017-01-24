package td4c;

import model.data.DataTable;
import model.feature.DiscreteFeature;
import model.feature.Feature;
import model.feature.FeatureKey;
import model.feature.FeatureValue;
import model.instance.Instance;
import model.instance.InstanceSetType;
import td4c.measures.IDistanceMeasure;

import java.util.*;

public class TD4CDiscretizator {

    private Map<String, Set<Instance>> classedInstances;
    private IDistanceMeasure distanceMeasure;

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

    public DataTable discrete(DataTable table, int bins) {
        DataTable discreteTable = new DataTable();
        List<DiscreteFeature> discreteFeatures = new ArrayList<>();
        Feature classFeature = null;
        for (Feature feature : table.getFeatures()) {
            if (feature.getKey().getKey().equals("Class")) {
                classFeature = feature;
            } else {
                DiscreteFeature discreteFeature = discrete(feature, bins);
                discreteFeature.setDataTable(discreteTable);
                discreteFeatures.add(discreteFeature);
            }
        }
        discreteFeatures.sort(DiscreteFeature::compareTo);
        Collections.reverse(discreteFeatures);
        for (DiscreteFeature discreteFeature : discreteFeatures) {
            discreteTable.put(discreteFeature);
        }
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
        DiscreteFeature discreteFeature = new DiscreteFeature(new FeatureKey<Object, Integer>(feature.getKey().getKey(), 0), feature.getDataTable());
        discreteFeature.setCutoffs(cutoffs);
        discreteFeature.setDistanceMeasure(evaluateCutoffs(feature, cutoffs));
        for (Instance instance : feature.getAllConcritInstances()) {
            Integer value = feature.getValue(instance).getValue();
            int discreteIndex = 0;
            for (Integer cutoff : cutoffs) {
                if (value < cutoff) {
                    break;
                }
                discreteIndex++;
            }
            discreteFeature.setValue(instance, new FeatureValue<>(discreteIndex));
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
                        if (feature.getValue(instance).getValue() < cutoff) {
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
        for (FeatureValue<Integer> featureValue : feature.getAllValues()) {
            optionalCutoffs.add(featureValue.getValue());
        }
        optionalCutoffs.sort(Integer::compareTo);
        return optionalCutoffs;
    }
}
