package sequence;

import model.data.DataTable;
import model.instance.DumpInstance;
import model.memory.Sequence;

import java.util.List;
import java.util.Set;

public interface ISequenceCounter {

    public void countSequences(DataTable dataTable, List<DumpInstance> instances, Set<Sequence> sequences);
}
