package model.data;

import model.feature.Feature;
import model.instance.Instance;
import model.instance.InstanceSetType;

import java.util.*;

public class DataTable {

    private final Set<Instance> instances;
    private final Set<Instance> trainInstances;
    private final Set<Instance> testInstances;
    private final Set<Feature> features;
    private final Map<Object, Feature> featureMap;
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

    public <S> void put(Instance instance, Object key, S value) {
        addInstance(instance);
        getCreateFeature(key, 0).setValue(instance, value);
        if (value instanceof Integer) {
            Integer intValue = (Integer) value;
            Integer maxValue = maxValues.get(instance);
            if (maxValue == null || intValue > maxValue) {
                maxValues.put(instance, intValue);
            }
        }
        inverseDocumentFrequencies = new HashMap<>();
    }

    public <S> void put(Feature<S> feature) {
        features.add(feature);
        featureMap.put(feature.getKey(), feature);
        for (Instance instance : feature.getAllConcritInstances()) {
            addInstance(instance);
            if (feature.getValue(instance) instanceof Integer) {
                Integer value = (Integer) feature.getValue(instance);
                Integer maxValue = maxValues.get(instance);
                if (maxValue == null || value > maxValue) {
                    maxValues.put(instance, value);
                }
            }
        }
        inverseDocumentFrequencies = new HashMap<>();
    }

    public <S> boolean putIfFeatureExists(Instance instance, S key, S value) {
        if (featureMap.containsKey(key)) {
            put(instance, key, value);
            return true;
        } else {
            addInstance(instance);
            return false;
        }
    }

    private void removeFeature(Feature feature) {
        features.remove(feature);
        featureMap.remove(feature.getKey());
    }

    private void addInstance(Instance instance) {
        instances.add(instance);
        if (instance.getSetType().equals(InstanceSetType.TRAIN_SET)) {
            trainInstances.add(instance);
        } else {
            testInstances.add(instance);
        }
    }

    public Feature getFeature(Object key) {
        return featureMap.get(key);
    }

    public Set<Instance> getInstances() {
        return instances;
    }

    public Set<Feature> getFeatures() {
        return features;
    }

    private Feature getCreateFeature(Object key) {
        return getCreateFeature(key, 0);
    }

    private Feature getCreateFeature(Object key, Object defaultValue) {
        Feature feature = featureMap.get(key);
        if (feature == null) {
            // TODO - change to this instead of null.
            feature = new Feature(key, defaultValue, null);
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

    public double getTimeFrequencyValue(Instance instance, Feature<Integer> feature) {
        Integer value = feature.getValue(instance);
        return value.doubleValue() / getInstanceMaximumTermFrequency(instance);
    }

    public double getTimeFrequencyInverseDocumentFrequencyValue(Instance instance, Feature<Integer> feature) {
        double timeFrequencyValue = getTimeFrequencyValue(instance, feature);
        return timeFrequencyValue * getInverseDocumentFrequency(feature);
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
            if (feature.getBinaryValue(instance).equals(1)) {
                countTrainDocumentWithFeature++;
            }
        }
        idf = Math.log(((double) trainInstances.size()) / countTrainDocumentWithFeature) / Math.log(2);
        inverseDocumentFrequencies.put(feature, idf);
        return idf;
    }

    public void removeCorrelatedFeatures(double threshold) {
        Set<Feature> featuresToRemove = new HashSet<>();
        for (Feature testedFeature : features) {
            for (Feature approvedFeature : features) {
                if (featuresToRemove.contains(approvedFeature)) {
                    continue;
                }
                if (testedFeature == approvedFeature) {
                    break;
                }
                if (approvedFeature.correlationRatio(testedFeature) > threshold) {
                    featuresToRemove.add(testedFeature);
                }
            }
        }
        for (Feature feature : featuresToRemove) {
            removeFeature(feature);
        }
    }
}
