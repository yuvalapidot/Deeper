package sequence;

import model.PseudoCallList;
import model.memory.Call;
import model.memory.Sequence;

import java.util.*;

public class PrefixSpanSequenceFinder extends AbstractSequenceFinder {

    public PrefixSpanSequenceFinder(int minimumSupport, int maximumSupport, int minimumSequenceLength, int maximumSequenceLength) {
        super(minimumSupport, maximumSupport, minimumSequenceLength, maximumSequenceLength);
    }

    public Map<Sequence, Integer> generateSubSequences(List<List<Call>> sequences) {
        List<PseudoCallList> pseudoSequences = toPseudoCallList(sequences);
        return generateSubSequences(pseudoSequences, new Sequence());
    }

    private Map<Sequence, Integer> generateSubSequences(List<PseudoCallList> sequences, Sequence prefix) {
        Map<Sequence, Integer> subSequences = new LinkedHashMap<>();
        if (prefix.size() == maximumSequenceLength) {
            subSequences.put(prefix, sequences.size());
            return subSequences;
        }
        Set<Call> alphaBet = getAlphaBet(sequences);
        for (Call call : alphaBet) {
            Sequence prefixSequence = new Sequence(prefix, call);
            List<PseudoCallList> projectedSequences = projectPseudoPostfixes(sequences, call);
            subSequences.putAll(generateSubSequences(projectedSequences, prefixSequence));
        }
        if (prefix.size() >= minimumSequenceLength) {
            subSequences.put(prefix, sequences.size());
        }
        return subSequences;
    }

    private List<PseudoCallList> projectPseudoPostfixes(List<PseudoCallList> sequences, Call call) {
        List<PseudoCallList> projectedSequences = new ArrayList<>();
        for (PseudoCallList sequence : sequences) {
            for (int i = sequence.getPointer(); i < sequence.getCalls().size(); i++) {
                if (call.equals(sequence.getCalls().get(i))) {
                    projectedSequences.add(new PseudoCallList(sequence.getCalls(), i + 1));
                    break;
                }
            }
        }
        return projectedSequences;
    }

}
