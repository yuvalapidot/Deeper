package creator;

import extractor.IFeatureExtractor;
import extractor.MultipleFeatureExtractor;
import model.data.DataTable;
import model.instance.DumpInstance;
import model.instance.Instance;
import model.memory.Dump;
import model.memory.Process;

import java.util.List;

public class DumpToDataTableCreator extends DataTableCreator {

    private List<DumpInstance> instances;

    public DumpToDataTableCreator(List<DumpInstance> instances) {
        super();
        this.instances = instances;
    }

    public DumpToDataTableCreator(List<DumpInstance> instances, List<IFeatureExtractor<DumpInstance>> extractors) {
        super(extractors);
        this.instances = instances;
    }

    @Override
    public DataTable createDataTable() {
        IFeatureExtractor<DumpInstance> extractor = new MultipleFeatureExtractor<>(extractors);
        extractor.setInstances(instances);
        DataTable table = extractor.extract();
        addThreadsAndProcesses(table);
        addClassifications(table, instances);
        return table;
    }

    @Override
    public void createDataTableToDataBase() {
        IFeatureExtractor<DumpInstance> extractor = new MultipleFeatureExtractor<>(extractors);
        extractor.setInstances(instances);
        extractor.extractToDataBase();
    }

    private void addClassifications(DataTable table, List<DumpInstance> instances) {
        String classFeatureKey = "Class";
        for (DumpInstance instance : instances) {
            table.put(instance, classFeatureKey, instance.getClassification());
        }
    }

    private void addThreadsAndProcesses(DataTable table) {
        for (Instance instance : table.getInstances()) {
            Dump dump = (Dump) instance.getInstance();
            table.put(instance, "Number of Processes", dump.getProcesses().size());
            int threadCount = 0;
            for (Process process : dump.getProcesses()) {
                threadCount += process.getThreads().size();
            }
            table.put(instance, "Number of Threads", threadCount);
        }
    }

}
