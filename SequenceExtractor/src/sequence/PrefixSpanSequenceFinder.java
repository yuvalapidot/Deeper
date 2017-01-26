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

    public void generateSubSequences(Map<Sequence, List<Pair<DumpInstance, Integer>>> map, List<DumpInstance> dumps, List<List<Call>> sequences, boolean saveToDataBase) {
        List<PseudoCallList> pseudoSequences = toPseudoCallList(dumps, sequences);
        generateSubSequences(map, new Sequence(), pseudoSequences);
        if (saveToDataBase) {
            putInDataBase(map);
        }
//        ObjectMapper mapper = new ObjectMapper();
//        try {
//            mapper.writeValue(new File("try.json"), map);
//        } catch (IOException e) {
//            System.out.println("Json writing did not work");
//        }
    }

    private void putInDataBase(Map<Sequence, List<Pair<DumpInstance, Integer>>> map) {
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

    private void generateSubSequences(Map<Sequence, List<Pair<DumpInstance, Integer>>> map, Sequence prefix, List<PseudoCallList> sequences) {
        if (prefix.size() >= minimumSequenceLength) {
            List<Pair<DumpInstance, Integer>> pairList;
            if ((pairList = map.get(prefix)) == null) {
                pairList = new ArrayList<>();
                map.put(prefix, pairList);
            }
            List<DumpInstance> dumpInstances = extractDumpList(sequences);
            for (DumpInstance dump : new HashSet<>(dumpInstances)) {
                int count = Collections.frequency(dumpInstances, dump);
                pairList.add(new Pair<>(dump, count));
            }
        }
        if (prefix.size() == maximumSequenceLength) {
            return;
        }
        Set<Call> alphaBet = getAlphaBet(sequences);
        for (Call call : alphaBet) {
            Sequence prefixSequence = new Sequence(prefix, call);
            List<PseudoCallList> projectedSequences = projectPseudoPostfixes(sequences, call);
            generateSubSequences(map , prefixSequence, projectedSequences);
        }
    }

    private void generateSubSequencesIter(Map<Sequence, List<Pair<DumpInstance, Integer>>> map, List<PseudoCallList> sequences) {
        Sequence prefix = new Sequence();
        Queue<Pair<Sequence, List<PseudoCallList>>> queue = new LinkedList<>();
        queue.add(new Pair<>(prefix, sequences));
        while (!queue.isEmpty()) {
            Pair<Sequence, List<PseudoCallList>> pair = queue.poll();
            prefix = pair.getKey();
            sequences = pair.getValue();
            if (prefix.size() >= minimumSequenceLength) {
                List<Pair<DumpInstance, Integer>> pairList;
                if ((pairList = map.get(prefix)) == null) {
                    pairList = new ArrayList<>();
                    map.put(prefix, pairList);
                }
                List<DumpInstance> dumpInstances = extractDumpList(sequences);
                for (DumpInstance dump : new HashSet<>(dumpInstances)) {
                    int count = Collections.frequency(dumpInstances, dump);
                    pairList.add(new Pair<>(dump, count));
                }
            }
            if (prefix.size() < maximumSequenceLength) {
                Set<Call> alphaBet = getAlphaBet(sequences);
                for (Call call : alphaBet) {
                    Sequence prefixSequence = new Sequence(prefix, call);
                    List<PseudoCallList> projectedSequences = projectPseudoPostfixes(sequences, call);
                    queue.add(new Pair<>(prefixSequence, projectedSequences));
                }
            }
        }
    }

    private List<PseudoCallList> projectPseudoPostfixes(List<PseudoCallList> sequences, Call call) {
        List<PseudoCallList> projectedSequences = new ArrayList<>(sequences.size());
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
