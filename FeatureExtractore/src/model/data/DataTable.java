package model.data;

import model.feature.Feature;
import model.feature.FeatureKey;
import model.feature.FeatureValue;
import model.feature.Instance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataTable {

    List<Instance> instances;
    Map<FeatureKey, Feature> features;

    public DataTable() {
        instances = new ArrayList<>();
        features = new HashMap<>();
    }

    public <T> void put(Instance instance, FeatureKey<?, T> featureKey, FeatureValue<T> featureValue) {
        if (!instances.contains(instance)) {
            instances.add(instance);
        }
        if (!features.keySet().contains(featureKey)) {
            features.put(featureKey, new Feature<T>());
        }
        features.get(featureKey).setValue(instance, featureValue);
    }

    public String csvString() {
        StringBuilder builder = new StringBuilder();
        return builder.toString();
    }
}
