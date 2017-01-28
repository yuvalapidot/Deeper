package model.feature;

import model.data.DataTable;

import java.util.List;

public class DiscreteFeature extends RankedFeature<Integer> {

    private List<Integer> cutoffs;

    public DiscreteFeature(Object key, Integer defaultValue, DataTable dataTable) {
        super(key, defaultValue, dataTable);
    }

    public List<Integer> getCutoffs() {
        return cutoffs;
    }

    public void setCutoffs(List<Integer> cutoffs) {
        this.cutoffs = cutoffs;
    }
}
