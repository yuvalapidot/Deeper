package model.feature;

import java.util.HashMap;
import java.util.Map;

public class Feature <S> {

    private final Map<Instance, FeatureValue<S>> values;

    public Feature() {
        values = new HashMap<>();
    }

    public Map<Instance, FeatureValue<S>> getValues() {
        return values;
    }

    public void setValue(Instance instance, FeatureValue<S> value) {
        values.put(instance, value);
    }

}
