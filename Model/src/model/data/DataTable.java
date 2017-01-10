package model.data;

import model.feature.BinaryFeatureValue;
import model.feature.Feature;
import model.feature.FeatureKey;
import model.feature.FeatureValue;
import model.instance.Instance;
import model.instance.InstanceSetType;

import java.util.*;

public class DataTable {

    private final Set<Instance> instances;
    private final Set<Instance> trainInstances;
    private final Set<Instance> testInstances;
    private final Set<Feature> features;
    private final Map<FeatureKey, Feature> featureMap;
    private final Map<Instance, Integer> maxValues;

    private Map<Feature, Double> inverseDocumentFrequencies;

    public DataTable() {
        instances = new LinkedHashSet<>();
        features = new LinkedHashSet<>();
        featureMap = new LinkedHashMap<>();
        maxValues = new HashMap<>();
        inverseDocumentFrequencies = new HashMap<>();
        trainInstances = new HashSet<>();
        testInstances = new HashSet<>();
    }

    public <S> void put(Instance instance, FeatureKey<?, S> featureKey, FeatureValue<S> featureValue) {
        addInstance(instance);
        getCreateFeature(featureKey).setValue(instance, featureValue);
        if (featureValue.getValue() instanceof Integer) {
            Integer value = (Integer) featureValue.getValue();
            Integer maxValue = maxValues.get(instance);
            if (maxValue == null || value > maxValue) {
                maxValues.put(instance, value);
            }
        }
        inverseDocumentFrequencies = new HashMap<>();
    }

    public <S> boolean putIfFeatureExists(Instance instance, FeatureKey<?, S> featureKey, FeatureValue<S> featureValue) {
        if (featureMap.containsKey(featureKey)) {
            put(instance, featureKey, featureValue);
            return true;
        } else {
            addInstance(instance);
            return false;
        }
    }

    private void addInstance(Instance instance) {
        instances.add(instance);
        if (instance.getSetType().equals(InstanceSetType.TRAIN_SET)) {
            trainInstances.add(instance);
        } else {
            testInstances.add(instance);
        }
    }

    public Feature getFeature(FeatureKey key) {
        return featureMap.get(key);
    }

    public Set<Instance> getInstances() {
        return instances;
    }

    public Set<Feature> getFeatures() {
        return features;
    }

    private Feature getCreateFeature(FeatureKey key) {
        Feature feature = featureMap.get(key);
        if (feature == null) {
            feature = new Feature(key, this);
            features.add(feature);
            featureMap.put(key, feature);
        }
        return feature;
    }

//    public void append(DataTable table) {
//        this.instances.addAll(table.instances);
//        for (Feature newFeature : features) {
//            Feature feature;
//            if ((feature = featureMap.get(newFeature.getKey())) != null) {
//                feature.append(newFeature);
//            } else {
//                features.add(newFeature);
//                featureMap.put(newFeature.getKey(), newFeature);
//            }
//        }
//    }

    public FeatureValue getTimeFrequencyValue(Instance instance, Feature feature) {
        FeatureValue value = feature.getValue(instance);
        if (value.getValue() instanceof Integer) {
            return new FeatureValue<>(((Integer) value.getValue()).doubleValue() / getInstanceMaximumTermFrequency(instance));
        } else {
            return value;
        }
    }

    public FeatureValue getTimeFrequencyInverseDocumentFrequencyValue(Instance instance, Feature feature) {
        FeatureValue timeFrequencyValue = getTimeFrequencyValue(instance, feature);
        if (timeFrequencyValue.getValue() instanceof Double) {
            return new FeatureValue<>(((Double) timeFrequencyValue.getValue()) * getInverseDocumentFrequency(feature));
        } else {
            return timeFrequencyValue;
        }
    }

    private int getInstanceMaximumTermFrequency(Instance instance) {
        Integer value = maxValues.get(instance);
        if (value == null) {
            return 0;
        }
        return value;
    }

    private double getInverseDocumentFrequency(Feature feature) {
        Double idf = inverseDocumentFrequencies.get(feature);
        if (idf != null) {
            return idf;
        }
        int countTrainDocumentWithFeature = 0;
        for (Instance instance : trainInstances) {
            if (feature.getBinaryValue(instance).equals(BinaryFeatureValue.TRUE_VALUE)) {
                countTrainDocumentWithFeature++;
            }
        }
        idf = Math.log(((double) trainInstances.size()) / countTrainDocumentWithFeature) / Math.log(2);
        inverseDocumentFrequencies.put(feature, idf);
        return idf;
    }
}
