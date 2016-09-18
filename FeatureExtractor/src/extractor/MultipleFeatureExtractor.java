package extractor;

import model.data.DataTable;
import model.instance.Instance;

import java.util.ArrayList;
import java.util.List;

public class MultipleFeatureExtractor<T extends Instance> extends AbstractFeatureExtractor<T> {

    private List<IFeatureExtractor<T>> extractors;

    public MultipleFeatureExtractor() {
        this(new ArrayList<>());
    }

    public MultipleFeatureExtractor(List<IFeatureExtractor<T>> extractors) {
        this.extractors = extractors;
    }

    public void addExtractor(IFeatureExtractor<T> extractor) {
        extractors.add(extractor);
    }

    @Override
    public void extract(DataTable table) {
        for (IFeatureExtractor<T> extractor : extractors) {
            extractor.setInstances(instances);
            extractor.extract(table);
        }
    }

    @Override
    public DataTable extract() {
        DataTable table = new DataTable();
        extract(table);
        return table;
    }
}
