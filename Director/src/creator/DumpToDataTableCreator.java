package creator;

import extractor.IFeatureExtractor;
import extractor.MultipleFeatureExtractor;
import model.data.DataTable;
import model.feature.FeatureKey;
import model.feature.FeatureValue;
import model.instance.DumpInstance;
import model.memory.Dump;

import java.util.List;

public class DumpToDataTableCreator extends DataTableCreator {

    private List<Dump> dumps;

    public DumpToDataTableCreator(List<Dump> dumps) {
        super();
        this.dumps = dumps;
    }

    public DumpToDataTableCreator(List<Dump> dumps, List<IFeatureExtractor<Dump>> extractors) {
        super(extractors);
        this.dumps = dumps;
    }

    @Override
    public DataTable createDataTable() {
        IFeatureExtractor<Dump> extractor = new MultipleFeatureExtractor<>(extractors);
        extractor.setInstances(dumps);
        DataTable table = extractor.extract();
        addClassifications(table, dumps);
        return table;
    }

    private void addClassifications(DataTable table, List<Dump> dumps) {
        FeatureKey<String, String> classFeatureKey = new FeatureKey<>("Class", "Unknown");
        for (Dump dump : dumps) {
            table.put(new DumpInstance(dump), classFeatureKey, new FeatureValue<>(dump.getClassification()));
        }
    }

}
