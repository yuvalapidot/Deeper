package model.feature;

import model.memory.Sequence;

public class SequenceFeatureKey extends FeatureKey<Sequence, Integer> {

    public SequenceFeatureKey(Sequence feature) {
        super(feature, 0);
    }

    public SequenceFeatureKey(Sequence feature, Integer defaultValue) {
        super(feature, defaultValue);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
