package creator;

import extractor.IFeatureExtractor;
import model.data.DataTable;
import model.memory.Dump;

import java.util.ArrayList;
import java.util.List;

public abstract class DataTableCreator {

    protected List<IFeatureExtractor<Dump>> extractors;

    public DataTableCreator() {
        this(new ArrayList<>());
    }

    public DataTableCreator(List<IFeatureExtractor<Dump>> extractors) {
        this.extractors = extractors;
    }

    public void addExtractor(IFeatureExtractor<Dump> extractor) {
        extractors.add(extractor);
    }

    public abstract DataTable createDataTable();
}
