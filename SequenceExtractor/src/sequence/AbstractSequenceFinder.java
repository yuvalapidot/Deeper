package sequence;

import model.PseudoCallList;
import model.memory.Call;
import model.memory.Sequence;

import java.util.*;

abstract class AbstractSequenceFinder implements ISequenceFinder {

    int minimumSupport;
    int maximumSupport;
    int minimumSequenceLength;
    int maximumSequenceLength;

    AbstractSequenceFinder(int minimumSupport, int maximumSupport, int minimumSequenceLength, int maximumSequenceLength) {
        this.minimumSupport = minimumSupport;
        this.maximumSupport = maximumSupport;
        this.minimumSequenceLength = minimumSequenceLength;
        this.maximumSequenceLength = maximumSequenceLength;
    }

    List<PseudoCallList> toPseudoCallList(List<List<Call>> sequences) {
        List<PseudoCallList> pseudos = new ArrayList<>();
        for (List<Call> sequence : sequences) {
            pseudos.add(new PseudoCallList(sequence));
        }
        return pseudos;
    }

    Set<Call> getAlphaBet(List<PseudoCallList> sequences) {
        Map<Call, Integer> alphaBet = new LinkedHashMap<>();
        for (PseudoCallList sequence : sequences) {
            Set<Call> callSet = new HashSet<>();
            for (Call call : sequence) {
                if (callSet.add(call)) {
                    Integer value = alphaBet.get(call);
                    if (value == null) {
                        alphaBet.put(call, 1);
                    } else {
                        alphaBet.put(call, value + 1);
                    }
                }
            }
        }
        alphaBet = cleanCallNotSupported(alphaBet);
        return alphaBet.keySet();
    }

    private Map<Call, Integer> cleanCallNotSupported(Map<Call, Integer> map) {
        Map<Call, Integer> cleanedMap = new LinkedHashMap<>();
        for (Call call : map.keySet()) {
            int value = map.get(call);
            if (isSupported(value)) {
                cleanedMap.put(call, value);
            }
        }
        return cleanedMap;
    }

    Map<Sequence, Integer> cleanSequenceNotSupported(Map<Sequence, Integer> map) {
        Map<Sequence, Integer> cleanedMap = new LinkedHashMap<>();
        for (Sequence sequence : map.keySet()) {
            int value = map.get(sequence);
            if (isSupported(value)) {
                cleanedMap.put(sequence, value);
            }
        }
        return cleanedMap;
    }

    int countSubSequence(List<PseudoCallList> sequences, Sequence subSequence) {
        int count = 0;
        for (PseudoCallList sequence : sequences) {
            if (containsSubSequence(sequence, subSequence)) {
                count++;
            }
        }
        return count;
    }

    private boolean containsSubSequence(PseudoCallList sequence, Sequence subSequence) {
        List<Call> postfix = sequence.getCalls().subList(sequence.getPointer(), sequence.getCalls().size());
        for (Call call : subSequence.getCalls()) {
            int index = postfix.indexOf(call);
            if (index == -1) {
                return false;
            }
            if (index == postfix.size() - 1) {
                postfix = new ArrayList<>();
            } else {
                postfix = postfix.subList(index + 1, postfix.size());
            }
        }
        return true;
    }

    private boolean isSupported(int value) {
        return (value >= minimumSupport & value <= maximumSupport);
    }
}
