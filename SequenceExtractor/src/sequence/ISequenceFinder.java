package sequence;

import model.instance.DumpInstance;
import model.memory.Call;
import model.memory.Sequence;

import java.util.List;
import java.util.Map;

public interface ISequenceFinder {

    public Map<Sequence, List<DumpInstance>> generateSubSequences(List<DumpInstance> dumps, List<List<Call>> sequences);

    public void generateSubSequencesToDataBase(List<DumpInstance> dumps, List<List<Call>> sequences);
}
