package model.data;

import model.feature.Feature;
import model.feature.FeatureKey;
import model.feature.FeatureValue;
import model.feature.Instance;

import java.util.*;

public class DataTable {

    private final Set<Instance> instances;
    private final Set<Feature> features;
    private final Map<FeatureKey, Feature> featureMap;

    public DataTable() {
        instances = new LinkedHashSet<>();
        features = new LinkedHashSet<>();
        featureMap = new LinkedHashMap<>();
    }

    public <S> void put(Instance instance, FeatureKey<?, S> featureKey, FeatureValue<S> featureValue) {
        instances.add(instance);
        getFeature(featureKey).setValue(instance, featureValue);
    }

    public Set<Instance> getInstances() {
        return instances;
    }

    public Set<Feature> getFeatures() {
        return features;
    }

    private Feature getFeature(FeatureKey key) {
        Feature feature = featureMap.get(key);
        if (feature == null) {
            feature = new Feature(key);
            features.add(feature);
            featureMap.put(key, feature);
        }
        return feature;
    }

    public void append(DataTable table) {
        this.instances.addAll(table.instances);
        for (Feature newFeature : features) {
            Feature feature;
            if ((feature = featureMap.get(newFeature.getKey())) != null) {
                feature.append(newFeature);
            } else {
                features.add(newFeature);
                featureMap.put(newFeature.getKey(), newFeature);
            }
        }
    }
}
