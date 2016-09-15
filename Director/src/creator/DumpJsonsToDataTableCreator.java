package creator;

import extractor.IFeatureExtractor;
import extractor.MultipleFeatureExtractor;
import model.data.DataTable;
import model.feature.FeatureKey;
import model.feature.FeatureValue;
import model.instance.DumpInstance;
import model.memory.Dump;
import reader.JsonDumpReader;
import reader.JsonToDumpRequest;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class DumpJsonsToDataTableCreator extends DataTableCreator {

    private List<File> files;

    public DumpJsonsToDataTableCreator(List<File> files) {
        super();
        this.files = files;
    }

    public DumpJsonsToDataTableCreator(List<File> files, List<IFeatureExtractor<Dump>> extractors) {
        super(extractors);
        this.files = files;
    }

    @Override
    public DataTable createDataTable() {
        IFeatureExtractor<Dump> extractor = new MultipleFeatureExtractor<>(extractors);
        List<Dump> dumps = getDumps();
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

    private List<Dump> getDumps() {
        JsonDumpReader reader = new JsonDumpReader();
        List<JsonToDumpRequest> requests = files.stream().map(file -> new JsonToDumpRequest(file)).collect(Collectors.toList());
        return reader.jsonsToDumps(requests);
    }

}
