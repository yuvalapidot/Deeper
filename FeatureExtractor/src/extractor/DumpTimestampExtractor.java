package extractor;

import model.data.DataTable;
import model.instance.DumpInstance;
import model.instance.Instance;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class DumpTimestampExtractor extends AbstractFeatureExtractor<DumpInstance> {

    @Override
    public void extract(DataTable table) {
        String formerDumpName = "";
        int timeStamp = 0;
        for (Instance instance : instances) {
            if (! instance.getType().equals(formerDumpName)) {
                formerDumpName = instance.getType();
                timeStamp = 0;
            }
            table.put(instance, "Timestamp", timeStamp);
            timeStamp += 15;
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
