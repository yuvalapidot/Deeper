package ranker.rankers;

import model.feature.CsvNumberRepresentation;
import model.feature.Feature;
import model.instance.Instance;
import model.instance.InstanceSetType;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class FishersScoreRanker implements IRankerMethod {

    @Override
    public double rank(Feature<Integer> feature, Set<Instance> instances, CsvNumberRepresentation representation) {
        List<String> classes = new ArrayList<>(findClasses(instances));
        if (classes.size() != 2) {
            return 1;
        }
        int[] counts = new int[classes.size()];
        double[] sums = new double[classes.size()];
        for (Instance instance : instances) {
            if (instance.getSetType().equals(InstanceSetType.TRAIN_SET)) {
                counts[classes.indexOf(instance.getClassification())]++;
                Object value = feature.getValue(instance, representation);
                if (value instanceof Integer) {
                    sums[classes.indexOf(instance.getClassification())] += (Integer) value;
                } else if (value instanceof Double) {
                    sums[classes.indexOf(instance.getClassification())] += (Double) value;
                }

            }
        }
        double[] averages = new double[classes.size()];
        for (int i = 0; i < classes.size(); i++) {
            averages[i] = sums[i] / (double) counts[i];
        }
        double[] variances = new double[classes.size()];
        for (Instance instance : instances) {
            if (instance.getSetType().equals(InstanceSetType.TRAIN_SET)) {
                Object value = feature.getValue(instance, representation);
                if (value instanceof Integer) {
                    double averageDiff = ((Integer) value) - averages[classes.indexOf(instance.getClassification())];
                    variances[classes.indexOf(instance.getClassification())] += averageDiff * averageDiff;
                } else if (value instanceof Double) {
                    double averageDiff = ((Double) value) - averages[classes.indexOf(instance.getClassification())];
                    variances[classes.indexOf(instance.getClassification())] += averageDiff * averageDiff;
                }
            }
        }
        for (int i = 0; i < classes.size(); i++) {
            variances[i] /= counts[i] - 1;
        }
        return Math.abs(averages[0] - averages[1]) / (Math.sqrt(variances[0]) + Math.sqrt(variances[1]));
    }

    private Set<String> findClasses(Set<Instance> instances) {
        Set<String> classes = new LinkedHashSet<>();
        for (Instance instance : instances) {
            classes.add(instance.getClassification());
        }
        return classes;
    }
}
