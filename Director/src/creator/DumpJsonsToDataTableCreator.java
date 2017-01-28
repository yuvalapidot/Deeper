package creator;

import extractor.IFeatureExtractor;
import extractor.MultipleFeatureExtractor;
import model.data.DataTable;
import model.instance.DumpInstance;
import model.memory.Dump;
import reader.JsonDumpReader;
import reader.JsonToDumpRequest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DumpJsonsToDataTableCreator extends DataTableCreator {

    private List<File> files;

    public DumpJsonsToDataTableCreator(List<File> files) {
        super();
        this.files = files;
    }

    public DumpJsonsToDataTableCreator(List<File> files, List<IFeatureExtractor<DumpInstance>> extractors) {
        super(extractors);
        this.files = files;
    }

    @Override
    public DataTable createDataTable() {
        IFeatureExtractor<DumpInstance> extractor = new MultipleFeatureExtractor<>(extractors);
        List<DumpInstance> instances = getDumpInstances(getDumps());
        extractor.setInstances(instances);
        DataTable table = extractor.extract();
        addClassifications(table, instances);
        return table;
    }

    @Override
    public void createDataTableToDataBase() {
        IFeatureExtractor<DumpInstance> extractor = new MultipleFeatureExtractor<>(extractors);
        List<DumpInstance> instances = getDumpInstances(getDumps());
        extractor.setInstances(instances);
        extractor.extractToDataBase();
    }

    private void addClassifications(DataTable table, List<DumpInstance> instances) {
        String classFeatureKey = "Class";
        for (DumpInstance instance : instances) {
            table.put(instance, classFeatureKey, instance.getClassification());
        }
    }

    private List<Dump> getDumps() {
        JsonDumpReader reader = new JsonDumpReader();
        List<JsonToDumpRequest> requests = files.stream().map(file -> new JsonToDumpRequest(file)).collect(Collectors.toList());
        return reader.jsonsToDumps(requests);
    }

    private static List<DumpInstance> getDumpInstances(List<Dump> dumps) {
        List<DumpInstance> instances = new ArrayList<>();
        for (Dump dump : dumps) {
            // TODO: adding set types to instances
            instances.add(new DumpInstance(dump));
        }
        return instances;
    }

}
