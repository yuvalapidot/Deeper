package extractor;

import model.data.DataTable;
import model.memory.CallGram;
import model.memory.Dump;

import java.util.List;

public class CallGramExtractor implements IFeatureExtractor {

    private final List<Dump> dumps;
    private final int n;

    public CallGramExtractor(List<Dump> dumps, int n) {
        this.dumps = dumps;
        this.n = n;
    }

    @Override
    public void extract(DataTable table) {
        for (Dump dump : dumps) {

        }
    }

    @Override
    public DataTable extract() {
        DataTable table = new DataTable();
        extract(table);
        return table;
    }
}
