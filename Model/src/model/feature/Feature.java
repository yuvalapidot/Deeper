package model.feature;

import model.instance.Instance;

import java.util.LinkedHashMap;
import java.util.Map;

public class Feature <S> {

    private final FeatureKey<?, S> key;
    private final Map<Instance, FeatureValue<S>> values;

    public Feature(FeatureKey<?, S> key) {
        this.key = key;
        values = new LinkedHashMap<>();
    }

    public FeatureValue<S> getValue(Instance instance) {
        FeatureValue<S> value = values.get(instance);
        if (value == null) {
            return new FeatureValue<>(key.getDefaultValue());
        }
        return value;
    }

    public FeatureValue getBinaryValue(Instance instance) {
        if (key.getDefaultValue() instanceof Number) {
            return (values.containsKey(instance)) ? BinaryFeatureValue.TRUE_VALUE : BinaryFeatureValue.FALSE_VALUE;
        }
        return getValue(instance);
    }

    public void setValue(Instance instance, FeatureValue<S> value) {
        values.put(instance, value);
    }

    public FeatureKey<?, S> getKey() {
        return key;
    }

    public void append(Feature<S> feature) {
        values.putAll(feature.values);
    }

    public int size() {
        return values.size();
    }
}
