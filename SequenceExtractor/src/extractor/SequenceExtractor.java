package extractor;

import model.data.DataTable;
import model.instance.DumpInstance;
import model.instance.InstanceSetType;
import model.memory.*;
import model.memory.Process;
import model.memory.Thread;
import sequence.ISequenceCounter;
import sequence.ISequenceFinder;
import sequence.NonContiguousSequenceCounter;
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
    public void extract(DataTable table) {
        Set<Sequence> sequences = getAllTrainSequences();
        ISequenceCounter counter = new NonContiguousSequenceCounter();
        counter.countSequences(table, instances, sequences);
    }

    private Set<Sequence> getAllTrainSequences() {
        Set<Sequence> sequences = new LinkedHashSet<>();
        ISequenceFinder finder = new PrefixSpanSequenceFinder(minimumSupport, maximumSupport, minimumSequenceLength, maximumSequenceLength);
        for (Set<Dump> dumpBatch : getDumpsBatches()) {
            sequences.addAll(getAllDumpsSequences(dumpBatch, finder));
        }
        return sequences;
    }

    private List<Set<Dump>> getDumpsBatches() {
        List<Set<Dump>> batches = new ArrayList<>();
        int counter = 0;
        Set<Dump> batch = new LinkedHashSet<>();
        for (DumpInstance instance : instances) {
            if (instance.getSetType().equals(InstanceSetType.TRAIN_SET)) {
                batch.add(instance.getInstance());
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

    private Set<Sequence> getAllDumpsSequences(Set<Dump> dumps, ISequenceFinder finder) {
        List<List<Call>> sequences = new ArrayList<>();
        for (Dump dump : dumps) {
            for (Process process : dump.getProcesses()) {
                for (Thread thread : process.getThreads()) {
                    sequences.add(thread.getCallStack().getCallList());
                }
            }
        }
        return finder.generateSubSequences(sequences).keySet();
    }

}
