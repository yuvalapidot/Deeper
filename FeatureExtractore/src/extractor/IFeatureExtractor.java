package extractor;

import model.data.DataTable;

public interface IFeatureExtractor {

    public void extract(DataTable table);

    public DataTable extract();
}
