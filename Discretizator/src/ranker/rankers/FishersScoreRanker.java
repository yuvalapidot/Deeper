package ranker.rankers;

import exceptions.MulticlassNotSupportedException;
import model.feature.Feature;
import model.instance.Instance;
import model.instance.InstanceSetType;

import java.util.*;

public class FishersScoreRanker implements IRankerMethod {

    @Override
    public double rank(Feature<Integer> feature, Set<Instance> instances) {
        List<String> classes = new ArrayList<>(findClasses(instances));
        if (classes.size() != 2) {
            throw new MulticlassNotSupportedException(classes.size() + " Classes were found during Fihser's score ranking. Expected exactly 2 classes");
        }
        int[] counts = new int[classes.size()];
        int[] sums = new int[classes.size()];
        for (Instance instance : instances) {
            if (instance.getSetType().equals(InstanceSetType.TRAIN_SET)) {
                counts[classes.indexOf(instance.getClassification())]++;
                sums[classes.indexOf(instance.getClassification())] += feature.getValue(instance);
            }
        }
        double[] averages = new double[classes.size()];
        for (int i = 0; i < classes.size(); i++) {
            averages[i] = sums[i] / (double) counts[i];
        }
        double[] variances = new double[classes.size()];
        for (Instance instance : instances) {
            if (instance.getSetType().equals(InstanceSetType.TRAIN_SET)) {
                double averageDiff = feature.getValue(instance) - averages[classes.indexOf(instance.getClassification())];
                variances[classes.indexOf(instance.getClassification())] += averageDiff * averageDiff;
            }
        }
        for (int i = 0; i < classes.size(); i++) {
            variances[i] /= counts[i] - 1;
        }
        return Math.abs(averages[0] - averages[1]) / (variances[0] + variances[1]);
    }

    private Set<String> findClasses(Set<Instance> instances) {
        Set<String> classes = new LinkedHashSet<>();
        for (Instance instance : instances) {
            classes.add(instance.getClassification());
        }
        return classes;
    }
}
