package model.feature;

import model.data.DataTable;
import model.instance.Instance;

import java.util.*;

public class Feature <S> {

    private final Object key;
    private final Map<Instance, S> values;
    private S defaultValue;
    private DataTable dataTable;

    public Feature(Object key, S defaultValue, DataTable dataTable) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.dataTable = dataTable;
        values = new LinkedHashMap<>();
    }

    public Feature(Feature<S> feature) {
        this.key = feature.key;
        this.values = feature.values;
        this.defaultValue = feature.defaultValue;
        this.dataTable = feature.dataTable;
    }

    public S getValue(Instance instance) {
        S value = values.get(instance);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public Integer getBinaryValue(Instance instance) {
        if (getValue(instance) != null) {
            return 1;
        }
        return 0;
    }

    public Collection<S> getAllValues() {
        return values.values();
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

    public void setValue(Instance instance, S value) {
        values.put(instance, value);
    }

    public Object getKey() {
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
