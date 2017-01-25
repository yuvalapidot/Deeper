package sequence;

import javafx.util.Pair;
import model.PseudoCallList;
import model.instance.DumpInstance;
import model.memory.Call;
import model.memory.Sequence;

import java.util.*;

abstract class AbstractSequenceFinder implements ISequenceFinder {

    int minimumSupport;
    int maximumSupport;
    int minimumSequenceLength;
    int maximumSequenceLength;

    public void generateSubSequences(Map<Sequence, List<Pair<DumpInstance, Integer>>> map, List<DumpInstance> dumps, List<List<Call>> sequences) {
        generateSubSequences(map, dumps, sequences, false);
    }

    public Map<Sequence, List<Pair<DumpInstance, Integer>>> generateSubSequences(List<DumpInstance> dumps, List<List<Call>> sequences, boolean saveToDataBase) {
        Map<Sequence, List<Pair<DumpInstance, Integer>>> map = new LinkedHashMap<>();
        generateSubSequences(map, dumps, sequences, saveToDataBase);
        return map;
    }

    public Map<Sequence, List<Pair<DumpInstance, Integer>>> generateSubSequences(List<DumpInstance> dumps, List<List<Call>> sequences) {
        return generateSubSequences(dumps, sequences, false);
    }

    AbstractSequenceFinder(int minimumSupport, int maximumSupport, int minimumSequenceLength, int maximumSequenceLength) {
        this.minimumSupport = minimumSupport;
        this.maximumSupport = maximumSupport;
        this.minimumSequenceLength = minimumSequenceLength;
        this.maximumSequenceLength = maximumSequenceLength;
    }

    List<PseudoCallList> toPseudoCallList(List<DumpInstance> dumps, List<List<Call>> sequences) {
        List<PseudoCallList> pseudos = new ArrayList<>();
        for (int i = 0; i < Math.min(sequences.size(), dumps.size()); i++) {
            pseudos.add(new PseudoCallList(dumps.get(i), sequences.get(i)));
        }
        return pseudos;
    }

    List<DumpInstance> extractDumpList(List<PseudoCallList> sequences) {
        List<DumpInstance> dumps = new ArrayList<>();
        for (PseudoCallList sequence : sequences) {
            dumps.add(sequence.getBelongsTo());
        }
        return dumps;
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

    Map<Sequence, List<DumpInstance>> cleanSequenceNotSupported(Map<Sequence, List<DumpInstance>> map) {
        Map<Sequence, List<DumpInstance>> cleanedMap = new LinkedHashMap<>();
        for (Sequence sequence : map.keySet()) {
            List<DumpInstance> dumps = map.get(sequence);
            if (isSupported(dumps.size())) {
                cleanedMap.put(sequence, dumps);
            }
        }
        return cleanedMap;
    }

    List<DumpInstance> getSubSequenceDumpSet(List<PseudoCallList> sequences, Sequence subSequence) {
        List<DumpInstance> dumps = new ArrayList<>();
        for (PseudoCallList sequence : sequences) {
            if (containsSubSequence(sequence, subSequence)) {
                dumps.add(sequence.getBelongsTo());
            }
        }
        return dumps;
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
