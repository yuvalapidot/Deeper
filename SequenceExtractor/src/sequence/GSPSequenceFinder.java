package sequence;

import model.PseudoCallList;
import model.memory.Call;
import model.memory.Sequence;

import java.util.*;

public class GSPSequenceFinder extends AbstractSequenceFinder {

    public GSPSequenceFinder(int minimumSupport, int maximumSupport, int minimumSequenceLength, int maximumSequenceLength) {
        super(minimumSupport, maximumSupport, minimumSequenceLength, maximumSequenceLength);
    }

    public Map<Sequence, Integer> generateSubSequences(List<List<Call>> sequences) {
        List<PseudoCallList> pseudoSequences = toPseudoCallList(sequences);
        Set<Call> alphaBet = getAlphaBet(pseudoSequences);
        Map<Sequence, Integer> subSequences = new LinkedHashMap<>();
        Map<Sequence, Integer> formerLayer = new LinkedHashMap<>();
        formerLayer.put(new Sequence(), minimumSupport);
        for (int i = 1; i <= maximumSequenceLength(pseudoSequences); i++) {
            Map<Sequence, Integer> nextLayer = new LinkedHashMap<>();
            for (Sequence subSequence : formerLayer.keySet()) {
                for (Call call : alphaBet) {
                    Sequence newSubSequence = new Sequence(subSequence);
                    newSubSequence.addCall(call);
                    nextLayer.put(newSubSequence, countSubSequence(pseudoSequences, newSubSequence));
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
