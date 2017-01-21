package td4c.measures;

import exceptions.DataUnsupervisedException;
import model.data.DataTable;
import model.feature.Feature;
import model.feature.FeatureKey;

import java.util.stream.IntStream;

abstract class AbstractDistanceMeasure implements IDistanceMeasure {

    protected double[] probability(int classPointer, int[][] bins) {
        double[] probability = new double[bins.length];
        for (int i = 0; i < bins.length; i++) {
            probability[i] = bins[i][classPointer] / IntStream.of(bins[i]).sum();
        }
        return probability;
    }
}
