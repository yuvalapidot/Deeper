package sequence;

import model.PseudoCallList;
import model.instance.DumpInstance;
import model.memory.Call;
import model.memory.Sequence;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GSPSequenceFinder extends AbstractSequenceFinder {

    public GSPSequenceFinder(int minimumSupport, int maximumSupport, int minimumSequenceLength, int maximumSequenceLength) {
        super(minimumSupport, maximumSupport, minimumSequenceLength, maximumSequenceLength);
    }

    public Map<Sequence, List<DumpInstance>> generateSubSequences(List<DumpInstance> dumps, List<List<Call>> sequences) {
        List<PseudoCallList> pseudoSequences = toPseudoCallList(dumps, sequences);
        Set<Call> alphaBet = getAlphaBet(pseudoSequences);
        Map<Sequence, List<DumpInstance>> subSequences = new LinkedHashMap<>();
        Map<Sequence, List<DumpInstance>> formerLayer = new LinkedHashMap<>();
        formerLayer.put(new Sequence(), dumps);
        for (int i = 1; i <= maximumSequenceLength(pseudoSequences); i++) {
            Map<Sequence, List<DumpInstance>> nextLayer = new LinkedHashMap<>();
            for (Sequence subSequence : formerLayer.keySet()) {
                for (Call call : alphaBet) {
                    Sequence newSubSequence = new Sequence(subSequence);
                    newSubSequence.addCall(call);
                    nextLayer.put(newSubSequence, getSubSequenceDumpSet(pseudoSequences, newSubSequence));
                }
            }
            nextLayer = cleanSequenceNotSupported(nextLayer);
            if (i > minimumSequenceLength) {
                subSequences.putAll(nextLayer);
            }
            formerLayer = nextLayer;
        }
        return subSequences;
    }

    @Override
    public void generateSubSequencesToDataBase(List<DumpInstance> dumps, List<List<Call>> sequences) {
        throw new NotImplementedException();
    }

    private int maximumSequenceLength(List<PseudoCallList> sequences) {
        int max = 0;
        for (PseudoCallList sequence: sequences) {
            if (sequence.size() > max) {
                max = sequence.size();
            }
        }
        return Math.min(max, maximumSequenceLength);
    }
}
