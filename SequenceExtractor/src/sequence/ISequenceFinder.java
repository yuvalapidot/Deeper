package sequence;

import model.memory.Call;
import model.memory.Sequence;

import java.util.List;
import java.util.Map;

public interface ISequenceFinder {

    public Map<Sequence, Integer> generateSubSequences(List<List<Call>> sequences);
}
