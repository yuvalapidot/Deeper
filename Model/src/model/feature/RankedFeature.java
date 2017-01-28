package model.feature;

import model.data.DataTable;

public class RankedFeature <S> extends Feature<S> implements Comparable<RankedFeature> {

    private double rank;

    public RankedFeature(Feature feature, double rank) {
        super(feature);
        this.rank = rank;
    }

    public RankedFeature(Object key, S defaultValue, DataTable dataTable) {
        super(key, defaultValue, dataTable);
    }

    public double getRank() {
        return rank;
    }

    public void setRank(double rank) {
        this.rank = rank;
    }

    @Override
    public int compareTo(RankedFeature o) {
        return rank < o.rank ? -1 : rank > o.rank ? +1 : 0;
    }
}
