package sequence;

import javafx.util.Pair;
import model.instance.DumpInstance;
import model.memory.Call;
import model.memory.Sequence;

import java.util.List;
import java.util.Map;

public interface ISequenceFinder {

    public void generateSubSequences(Map<Sequence, List<Pair<DumpInstance, Integer>>> map, List<DumpInstance> dumps, List<List<Call>> sequences, boolean saveToDataBase);

    public void generateSubSequences(Map<Sequence, List<Pair<DumpInstance, Integer>>> map, List<DumpInstance> dumps, List<List<Call>> sequences);

    public Map<Sequence, List<Pair<DumpInstance, Integer>>> generateSubSequences(List<DumpInstance> dumps, List<List<Call>> sequences, boolean saveToDataBase);

    public Map<Sequence, List<Pair<DumpInstance, Integer>>> generateSubSequences(List<DumpInstance> dumps, List<List<Call>> sequences);
}
