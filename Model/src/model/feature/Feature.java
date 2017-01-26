package model.feature;

import model.data.DataTable;
import model.instance.Instance;

import java.util.*;

public class Feature <S> {

    private final FeatureKey<?, S> key;
    private final Map<Instance, FeatureValue<S>> values;
    private DataTable dataTable;

    public Feature(FeatureKey<?, S> key, DataTable dataTable) {
        this.key = key;
        this.dataTable = dataTable;
        values = new LinkedHashMap<>();
    }

    public Feature(Feature feature) {
        this.key = feature.key;
        this.values = feature.values;
        this.dataTable = feature.dataTable;
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

    public Set<FeatureValue<S>> getAllValues() {
        return new LinkedHashSet<>(values.values());
    }

    public Set<Instance> getAllConcritInstances() {
        return values.keySet();
    }

    public List<Instance> getInstancesOfClassification(String classification) {
        List<Instance> instances = new ArrayList<>();
        for (Instance instance : values.keySet()) {
            if (instance.getClassification().equals(classification)) {
                instances.add(instance);
            }
        }
        return instances;
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

    public DataTable getDataTable() {
        return dataTable;
    }

    public void setDataTable(DataTable dataTable) {
        this.dataTable = dataTable;
    }
}
