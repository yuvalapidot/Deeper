package creator;

import extractor.IFeatureExtractor;
import model.data.DataTable;
import model.instance.DumpInstance;

import java.util.ArrayList;
import java.util.List;

public abstract class DataTableCreator {

    protected List<IFeatureExtractor<DumpInstance>> extractors;

    public DataTableCreator() {
        this(new ArrayList<>());
    }

    public DataTableCreator(List<IFeatureExtractor<DumpInstance>> extractors) {
        this.extractors = extractors;
    }

    public void addExtractor(IFeatureExtractor<DumpInstance> extractor) {
        extractors.add(extractor);
    }

    public abstract DataTable createDataTable();

    public abstract void createDataTableToDataBase();
}
