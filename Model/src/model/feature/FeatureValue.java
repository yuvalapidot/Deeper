package model.feature;

import java.util.HashMap;
import java.util.Map;

public class FeatureValue <T> {

    protected final T value;

    private static Map<FeatureValue, FeatureValue> lookupTable = new HashMap<>();

    public FeatureValue(T value) {
        this.value = value;
    }

    public static FeatureValue instance(FeatureValue featureValue) {
        FeatureValue existingFeatureValue = lookupTable.putIfAbsent(featureValue, featureValue);
        return existingFeatureValue == null ? featureValue : existingFeatureValue;
    }

    public T getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FeatureValue)) return false;

        FeatureValue<?> that = (FeatureValue<?>) o;

        return getValue() != null ? getValue().equals(that.getValue()) : that.getValue() == null;

    }

    @Override
    public int hashCode() {
        return getValue() != null ? getValue().hashCode() : 0;
    }

}
