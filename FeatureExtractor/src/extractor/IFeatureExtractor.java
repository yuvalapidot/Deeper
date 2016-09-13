package extractor;

import model.data.DataTable;

import java.util.List;

public interface IFeatureExtractor<T> {

    public void extract(DataTable table);

    public DataTable extract();

    public void setInstances(List<T> instances);
}
