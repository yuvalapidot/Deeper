package model.feature;

import model.memory.Sequence;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FeatureKey <T, S> {

    private final T key;
    private S defaultValue = null;

    private static Map<FeatureKey, FeatureKey> lookupTable = new HashMap<>();

    public FeatureKey(T key) {
        this.key = key;
    }

    public FeatureKey(T key, S defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public static FeatureKey instance(FeatureKey featureKey) {
        FeatureKey existingFeatureKey = lookupTable.putIfAbsent(featureKey, featureKey);
        return existingFeatureKey == null ? featureKey : existingFeatureKey;
    }

    public T getKey() {
        return key;
    }

    public S getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String toString() {
        return key.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FeatureKey)) return false;

        FeatureKey<?, ?> that = (FeatureKey<?, ?>) o;

        if (!getKey().equals(that.getKey())) return false;
        return getDefaultValue() != null ? getDefaultValue().equals(that.getDefaultValue()) : that.getDefaultValue() == null;

    }

    @Override
    public int hashCode() {
        int result = getKey().hashCode();
        result = 31 * result + (getDefaultValue() != null ? getDefaultValue().hashCode() : 0);
        return result;
    }
}
