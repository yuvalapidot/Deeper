package model.feature;

import model.data.DataTable;
import model.instance.Instance;

import java.util.*;

public class Feature <S> {

    private static Map<Instance, Integer> instanceMapping;

    private final Object key;
    private final S[] values;
    private S defaultValue;
    private DataTable dataTable;

    public static void initialize(List<? extends Instance> instances) {
        instanceMapping = new LinkedHashMap<>();
        for (int i = 0; i < instances.size(); i++) {
            instanceMapping.put(instances.get(i), i);
        }
    }

    public Feature(Object key, S defaultValue, DataTable dataTable) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.dataTable = dataTable;
        values = (S[]) new Object[instanceMapping.size()];
    }

    public Feature(Feature<S> feature) {
        this.key = feature.key;
        this.values = feature.values;
        this.defaultValue = feature.defaultValue;
        this.dataTable = feature.dataTable;
    }

    public S getValue(Instance instance) {
        S value = values[instanceMapping.get(instance)];
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    private S getValue(int index) {
        S value = values[index];
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public Object getValue(Instance instance, CsvNumberRepresentation representation) {
        DataTable table = getDataTable();
        Object value = getValue(instance);
        if (!(value instanceof Integer)) {
            return value;
        }
        if (representation.equals(CsvNumberRepresentation.Integer_Representation)) {
            return value;
        } else if (representation.equals(CsvNumberRepresentation.Binary_Representation)) {
            return getBinaryValue(instance);
        } else if (representation.equals(CsvNumberRepresentation.TF_Representation)) {
            return table.getTimeFrequencyValue(instance, this);
        } else if (representation.equals(CsvNumberRepresentation.TFIDF_Representation)) {
            return table.getTimeFrequencyInverseDocumentFrequencyValue(instance, this);
        } else {
            return value;
        }
    }

    public Integer getBinaryValue(Instance instance) {
        Object value = getValue(instance);
        if (value != null) {
            if (value instanceof Integer) {
                if ((Integer) value == 0) {
                    return 0;
                }
            }
            return 1;
        }
        return 0;
    }

    public Set<S> getAllValues() {
        Set set = new HashSet(Arrays.asList(values));
        if (set.remove(null)) {
            set.add(defaultValue);
        }
        return set;
    }

    public Set<Instance> getAllConcritInstances() {
        return instanceMapping.keySet();
    }

    public void setValue(Instance instance, S value) {
        values[instanceMapping.get(instance)] = value;
    }

    public Object getKey() {
        return key;
    }

    public int size() {
        return values.length;
    }

    public DataTable getDataTable() {
        return dataTable;
    }

    public void setDataTable(DataTable dataTable) {
        this.dataTable = dataTable;
    }

    public double correlationRatio(Feature other, CsvNumberRepresentation representation) {
        int counter = 0;
        for (int i : instanceMapping.values()) {
            if (getValue(i).equals(other.getValue(i))) {
                counter++;
            }
        }
        return counter / (double) instanceMapping.size();
    }
}
