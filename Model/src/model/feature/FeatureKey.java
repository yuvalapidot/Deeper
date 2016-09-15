package model.feature;

public class FeatureKey <T, S> {

    private final T key;
    private S defaultValue = null;

    public FeatureKey(T key) {
        this.key = key;
    }

    public FeatureKey(T key, S defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
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
