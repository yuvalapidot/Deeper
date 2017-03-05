package extractor;

import model.data.DataTable;
import model.instance.DumpInstance;
import model.instance.Instance;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class DumpStateExtractor extends AbstractFeatureExtractor<DumpInstance> {

    @Override
    public void extract(DataTable table) {
        for (Instance instance : instances) {
            table.put(instance, "Scenario", ((DumpInstance) instance).getType());
        }
    }

    @Override
    public DataTable extract() {
        DataTable table = new DataTable();
        extract(table);
        return table;
    }

    @Override
    public void extractToDataBase() {
        throw new NotImplementedException();
    }
}
