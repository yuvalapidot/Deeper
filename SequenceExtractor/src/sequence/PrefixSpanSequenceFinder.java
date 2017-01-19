package sequence;

import dal.sql.sqlite.DataAccessLayer;
import dal.sql.sqlite.IDataAccessLayer;
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
        try (IDataAccessLayer dal = new DataAccessLayer()) {
            generateSubSequencesToDataBase(dal, new Sequence(), pseudoSequences);
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

    private void generateSubSequencesToDataBase(IDataAccessLayer dal, Sequence prefix, List<PseudoCallList> sequences) {
        if (prefix.size() >= minimumSequenceLength) {
            List<DumpInstance> dumpInstances = extractDumpList(sequences);
            dal.insertSequence(prefix);
            for (DumpInstance dump : new HashSet<>(dumpInstances)) {
                dal.insertDumpSequenceRelation(dump, prefix, Collections.frequency(dumpInstances, dump));
            }
        }
        if (prefix.size() == maximumSequenceLength) {
            return;
        }
        Set<Call> alphaBet = getAlphaBet(sequences);
        for (Call call : alphaBet) {
            Sequence prefixSequence = new Sequence(prefix, call);
            List<PseudoCallList> projectedSequences = projectPseudoPostfixes(sequences, call);
            generateSubSequencesToDataBase(dal , prefixSequence, projectedSequences);
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
