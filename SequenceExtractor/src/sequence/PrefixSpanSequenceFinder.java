package sequence;

import Model.SequenceData;
import dal.sql.sqlite.DataAccessLayer;
import dal.sql.sqlite.IDataAccessLayer;
import javafx.util.Pair;
import model.PseudoCallList;
import model.instance.DumpInstance;
import model.memory.Call;
import model.memory.Sequence;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class PrefixSpanSequenceFinder extends AbstractSequenceFinder {

    public PrefixSpanSequenceFinder(int minimumSupport, int maximumSupport, int minimumSequenceLength, int maximumSequenceLength) {
        super(minimumSupport, maximumSupport, minimumSequenceLength, maximumSequenceLength);
    }

    public Map<Sequence, List<DumpInstance>> generateSubSequences(List<DumpInstance> dumps, List<List<Call>> sequences) {
        List<PseudoCallList> pseudoSequences = toPseudoCallList(dumps, sequences);
        return generateSubSequences(pseudoSequences, new Sequence());
    }

    public void generateSubSequencesToDataBase(List<DumpInstance> dumps, List<List<Call>> sequences) {
        List<PseudoCallList> pseudoSequences = toPseudoCallList(dumps, sequences);
        Map<Sequence, List<Pair<DumpInstance, Integer>>> map = new LinkedHashMap<>();
        generateSubSequencesToDataBase(map, new Sequence(), pseudoSequences);
        try (IDataAccessLayer dal = new DataAccessLayer()) {
            Set<SequenceData> existingSequences = new HashSet<>(dal.selectSequences());
            List<Sequence> newSequences = new ArrayList<>();
            for (Sequence sequence : map.keySet()) {
                if (!existingSequences.contains(new SequenceData(sequence.toString(), sequence.size()))) {
                    newSequences.add(sequence);
                }
            }
            dal.insertSequencesBatch(newSequences);
            dal.insertDumpSequenceRelationBatch(map);
        } catch (SQLException | IOException ex) {
            throw new RuntimeException("Encountered an Exception", ex);
        }
    }

    private Map<Sequence, List<DumpInstance>> generateSubSequences(List<PseudoCallList> sequences, Sequence prefix) {
        Map<Sequence, List<DumpInstance>> subSequences = new LinkedHashMap<>();
        if (prefix.size() == maximumSequenceLength) {
            subSequences.put(prefix, extractDumpList(sequences));
            return subSequences;
        }
        Set<Call> alphaBet = getAlphaBet(sequences);
        for (Call call : alphaBet) {
            Sequence prefixSequence = new Sequence(prefix, call);
            List<PseudoCallList> projectedSequences = projectPseudoPostfixes(sequences, call);
            subSequences.putAll(generateSubSequences(projectedSequences, prefixSequence));
        }
        if (prefix.size() >= minimumSequenceLength) {
            subSequences.put(prefix, extractDumpList(sequences));
        }
        return subSequences;
    }

    private void generateSubSequencesToDataBase(Map<Sequence, List<Pair<DumpInstance, Integer>>> map, Sequence prefix, List<PseudoCallList> sequences) {
        if (prefix.size() >= minimumSequenceLength) {
//            if (!existingSequences.contains(new SequenceData(prefix.toString(), prefix.size()))) {
//                dal.insertSequence(prefix);
//            }
            List<Pair<DumpInstance, Integer>> pairList = new ArrayList<>();
            List<DumpInstance> dumpInstances = extractDumpList(sequences);
            for (DumpInstance dump : new HashSet<>(dumpInstances)) {
                int count = Collections.frequency(dumpInstances, dump);
                pairList.add(new Pair<DumpInstance, Integer>(dump, count));
//                dal.insertDumpSequenceRelation(dump, prefix, Collections.frequency(dumpInstances, dump));
            }
            map.put(prefix, pairList);
        }
        if (prefix.size() == maximumSequenceLength) {
            return;
        }
        Set<Call> alphaBet = getAlphaBet(sequences);
        for (Call call : alphaBet) {
            Sequence prefixSequence = new Sequence(prefix, call);
            List<PseudoCallList> projectedSequences = projectPseudoPostfixes(sequences, call);
            generateSubSequencesToDataBase(map , prefixSequence, projectedSequences);
        }
    }

    private List<PseudoCallList> projectPseudoPostfixes(List<PseudoCallList> sequences, Call call) {
        List<PseudoCallList> projectedSequences = new ArrayList<>();
        for (PseudoCallList sequence : sequences) {
            for (int i = sequence.getPointer(); i < sequence.getCalls().size(); i++) {
                if (call.equals(sequence.getCalls().get(i))) {
                    projectedSequences.add(new PseudoCallList(sequence.getBelongsTo(), sequence.getCalls(), i + 1));
                    break;
                }
            }
        }
        return projectedSequences;
    }

}
