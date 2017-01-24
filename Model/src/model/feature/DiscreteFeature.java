package model.feature;

import model.data.DataTable;

import java.util.List;

public class DiscreteFeature extends Feature<Integer> implements Comparable<DiscreteFeature> {

    private double distanceMeasure;
    private List<Integer> cutoffs;

    public DiscreteFeature(FeatureKey<?, Integer> key, DataTable dataTable) {
        super(key, dataTable);
    }

    public double getDistanceMeasure() {
        return distanceMeasure;
    }

    public void setDistanceMeasure(double distanceMeasure) {
        this.distanceMeasure = distanceMeasure;
    }

    public List<Integer> getCutoffs() {
        return cutoffs;
    }

    public void setCutoffs(List<Integer> cutoffs) {
        this.cutoffs = cutoffs;
    }

    @Override
    public int compareTo(DiscreteFeature o) {
        return distanceMeasure < o.distanceMeasure ? -1 : distanceMeasure > o.distanceMeasure ? +1 : 0;
    }
}
