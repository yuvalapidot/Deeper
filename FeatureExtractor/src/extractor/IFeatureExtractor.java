package extractor;

import model.data.DataTable;

import java.util.List;

public interface IFeatureExtractor<T> {

    void extract(DataTable table);

    DataTable extract();

    void setInstances(List<T> instances);
}
