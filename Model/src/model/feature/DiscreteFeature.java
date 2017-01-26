package model.feature;

import model.data.DataTable;

import java.util.List;

public class DiscreteFeature extends RankedFeature<Integer> {

    private List<Integer> cutoffs;

    public DiscreteFeature(FeatureKey<?, Integer> key, DataTable dataTable) {
        super(key, dataTable);
    }

    public List<Integer> getCutoffs() {
        return cutoffs;
    }

    public void setCutoffs(List<Integer> cutoffs) {
        this.cutoffs = cutoffs;
    }
}
