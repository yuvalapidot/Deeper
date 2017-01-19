package extractor;

import model.data.DataTable;
import model.feature.FeatureKey;
import model.feature.FeatureValue;
import model.instance.DumpInstance;
import model.instance.InstanceSetType;
import model.memory.*;
import model.memory.Process;
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

    private boolean trainDiffFeature = false;
    private boolean trainBenignDiffFeature = false;

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
        getAllTrainSequencesToDataBase();
    }

    @Override
    public void extract(DataTable table) {
        Map<Sequence, List<DumpInstance>> sequences = getAllTrainSequences();
        for (Sequence sequence : sequences.keySet()) {
            FeatureKey<Sequence, Integer> featureKey = new FeatureKey<>(sequence, 0);
            for (DumpInstance dumpInstance : new LinkedHashSet<>(sequences.get(sequence))) {
                FeatureValue<Integer> featureValue = new FeatureValue<>(Collections.frequency(sequences.get(sequence), dumpInstance));
                table.put(dumpInstance, featureKey, featureValue);
            }
        }
    }

    private Map<Sequence, List<DumpInstance>> getAllTrainSequences() {
        Map<Sequence, List<DumpInstance>> sequences = new LinkedHashMap<>();
        ISequenceFinder finder = new PrefixSpanSequenceFinder(minimumSupport, maximumSupport, minimumSequenceLength, maximumSequenceLength);
        for (Set<DumpInstance> dumpBatch : getDumpsBatches()) {
            Map<Sequence, List<DumpInstance>> batchSequences = getAllDumpsSequences(dumpBatch, finder);
            addSequencesToMap(sequences, batchSequences);
        }
        return sequences;
    }

    private void getAllTrainSequencesToDataBase() {
        ISequenceFinder finder = new PrefixSpanSequenceFinder(minimumSupport, maximumSupport, minimumSequenceLength, maximumSequenceLength);
        for (Set<DumpInstance> dumpBatch : getDumpsBatches()) {
            getAllDumpsSequencesToDataBase(dumpBatch, finder);
        }
    }

    private void addSequencesToMap(Map<Sequence, List<DumpInstance>> map, Map<Sequence, List<DumpInstance>> sequences) {
        for (Sequence sequence : sequences.keySet()) {
            if (map.containsKey(sequence)) {
                map.get(sequence).addAll(sequences.get(sequence));
            } else {
                map.put(sequence, sequences.get(sequence));
            }
        }
    }

    private List<Set<DumpInstance>> getDumpsBatches() {
        List<Set<DumpInstance>> batches = new ArrayList<>();
        int counter = 0;
        Set<DumpInstance> batch = new LinkedHashSet<>();
        for (DumpInstance instance : instances) {
            if (instance.getSetType().equals(InstanceSetType.TRAIN_SET)) {
                batch.add(instance);
                counter++;
                if (counter % batchSize == 0) {
                    batches.add(batch);
                    batch = new LinkedHashSet<>();
                }
            }
        }
        if (!batch.isEmpty()) {
            batches.add(batch);
        }
        return batches;
    }

    private Map<Sequence, List<DumpInstance>> getAllDumpsSequences(Set<DumpInstance> dumps, ISequenceFinder finder) {
        List<DumpInstance> dumpList = new ArrayList<>();
        List<List<Call>> sequences = new ArrayList<>();
        prepareDumpsAndSequences(dumps, dumpList, sequences);
        return finder.generateSubSequences(dumpList, sequences);
    }

    private void getAllDumpsSequencesToDataBase(Set<DumpInstance> dumps, ISequenceFinder finder) {
        List<DumpInstance> dumpList = new ArrayList<>();
        List<List<Call>> sequences = new ArrayList<>();
        prepareDumpsAndSequences(dumps, dumpList, sequences);
        finder.generateSubSequencesToDataBase(dumpList, sequences);
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
