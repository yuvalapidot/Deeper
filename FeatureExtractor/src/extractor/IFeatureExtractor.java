package extractor;

import model.data.DataTable;
import model.instance.Instance;

import java.util.List;

public interface IFeatureExtractor<T extends Instance> {

    void extract(DataTable table);

    DataTable extract();

    void extractToDataBase();

    void setInstances(List<T> instances);
}
