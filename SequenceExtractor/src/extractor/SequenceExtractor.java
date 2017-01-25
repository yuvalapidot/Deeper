package extractor;

import javafx.util.Pair;
import model.data.DataTable;
import model.feature.FeatureKey;
import model.feature.FeatureValue;
import model.instance.DumpInstance;
import model.memory.Call;
import model.memory.Process;
import model.memory.Sequence;
import model.memory.Thread;
import sequence.ISequenceFinder;
import sequence.PrefixSpanSequenceFinder;

import java.util.*;

public class SequenceExtractor extends AbstractFeatureExtractor<DumpInstance> {

    private int minimumSupport;
    private int maximumSupport;
    private int minimumSequenceLength;
    private int maximumSequenceLength;
    private int batchSize;

    public SequenceExtractor(int minimumSupport, int maximumSupport, int minimumSequenceLength, int maximumSequenceLength, int batchSize) {
        this.minimumSupport = minimumSupport;
        this.maximumSupport = maximumSupport;
        this.minimumSequenceLength = minimumSequenceLength;
        this.maximumSequenceLength = maximumSequenceLength;
        this.batchSize = batchSize;
    }

    @Override
    public DataTable extract() {
        DataTable table = new DataTable();
        extract(table);
        return table;
    }

    @Override
    public void extractToDataBase() {
        getAllSequences(true);
    }

    @Override
    public void extract(DataTable table) {
        Map<Sequence, List<Pair<DumpInstance, Integer>>> sequences = getAllSequences(false);
        for (Sequence sequence : sequences.keySet()) {
            FeatureKey<Sequence, Integer> featureKey = new FeatureKey<>(sequence, 0);
            for (Pair<DumpInstance, Integer> dumpInfo : sequences.get(sequence)) {
                FeatureValue<Integer> featureValue = new FeatureValue<>(dumpInfo.getValue());
                table.put(dumpInfo.getKey(), featureKey, featureValue);
            }
        }
    }

    private Map<Sequence, List<Pair<DumpInstance, Integer>>> getAllSequences(boolean saveToDataBase) {
        ISequenceFinder finder = new PrefixSpanSequenceFinder(minimumSupport, maximumSupport, minimumSequenceLength, maximumSequenceLength);
        Map<Sequence, List<Pair<DumpInstance, Integer>>> map = new LinkedHashMap<>();
        for (Set<DumpInstance> dumpBatch : getDumpsBatches()) {
            getAllDumpsSequences(map, dumpBatch, finder, saveToDataBase);
        }
        return map;
    }

    private List<Set<DumpInstance>> getDumpsBatches() {
        List<Set<DumpInstance>> batches = new ArrayList<>();
        int counter = 0;
        Set<DumpInstance> batch = new LinkedHashSet<>();
        for (DumpInstance instance : instances) {
                batch.add(instance);
                counter++;
                if (counter % batchSize == 0) {
                    batches.add(batch);
                    batch = new LinkedHashSet<>();
                }
        }
        if (!batch.isEmpty()) {
            batches.add(batch);
        }
        return batches;
    }

    private void getAllDumpsSequences(Map<Sequence, List<Pair<DumpInstance, Integer>>> map, Set<DumpInstance> dumps, ISequenceFinder finder, boolean saveToDataBase) {
        List<DumpInstance> dumpList = new ArrayList<>();
        List<List<Call>> sequences = new ArrayList<>();
        prepareDumpsAndSequences(dumps, dumpList, sequences);
        finder.generateSubSequences(map, dumpList, sequences, saveToDataBase);
    }

    private void prepareDumpsAndSequences(Set<DumpInstance> dumps, List<DumpInstance> dumpList, List<List<Call>> sequences) {
        for (DumpInstance dumpInstance : dumps) {
            for (Process process : dumpInstance.getInstance().getProcesses()) {
                for (Thread thread : process.getThreads()) {
                    dumpList.add(dumpInstance);
                    sequences.add(thread.getCallStack().getCallList());
                }
            }
        }
    }

}
