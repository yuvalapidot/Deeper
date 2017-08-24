package extractor;

import model.data.DataTable;
import model.instance.Instance;

import java.util.List;
import java.util.Map;

public interface IFeatureExtractor<T extends Instance> {

    void extract(DataTable table);

    DataTable extract();

    void extractToDataBase();

    void setInstances(List<T> instances);
    void setInstances(Map<? extends Object, List<T>> instances);
}
