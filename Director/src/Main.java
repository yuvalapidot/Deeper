import model.data.DataTable;
import model.instance.Instance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class Main {

    private static final Logger log = LogManager.getLogger(Main.class);

    private static final String jsonsDirectoryPath = "D:\\Dropbox\\Deeper\\Jsons";
    private static final String datasetOutputPath = "D:\\Dropbox\\Deeper\\Datasets\\";

    private static DataTable getNGramDataTable(List<Instance> instances, int[] ns) {
        return getHybridDataTable(instances, ns, 0, 0, 0, 0, BatchType.SINGLE_BATCH);
    }

    private static DataTable getSequenceDataTable(List<Instance> instances, int minSequenceLength, int maxSequenceLength, int minSupport, int maxSupport, BatchType batch) {
        return getHybridDataTable(instances, null, minSequenceLength, maxSequenceLength, minSupport, maxSupport, batch);
    }

    private static DataTable getHybridDataTable(List<Instance> instances, int[] ns, int minSequenceLength, int maxSequenceLength, int minSupport, int maxSupport, BatchType batch) {
        return null;
    }

    private enum BatchType {
        SINGLE_BATCH(1),
        STATE_BATCH(100),
        CLASS_BATCH(500),
        WHOLE_BATCH(1000);

        private final int size;

        BatchType(int size) {
            this.size = size;
        }

        public int getSize() {
            return size;
        }
    }
}
