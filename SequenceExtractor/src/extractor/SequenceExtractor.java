package extractor;

import model.data.DataTable;
import model.feature.Feature;
import model.feature.FeatureKey;
import model.feature.SequenceFeatureKey;
import model.feature.FeatureValue;
import model.instance.DumpInstance;
import model.instance.InstanceSetType;
import model.memory.*;
import model.memory.Process;
import model.memory.Thread;
import sequence.GSPSequenceFinder;
import sequence.ISequenceFinder;
import sequence.PrefixSpanSequenceFinder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SequenceExtractor extends AbstractFeatureExtractor<DumpInstance> {

    private int minimumSupport;
    private int maximumSupport;
    private int minimumSequenceLength;
    private int maximumSequenceLength;

    private boolean trainDiffFeature = false;
    private boolean trainBenignDiffFeature = false;

    public SequenceExtractor(int minimumSupport, int maximumSupport, int minimumSequenceLength, int maximumSequenceLength) {
        this.minimumSupport = minimumSupport;
        this.maximumSupport = maximumSupport;
        this.minimumSequenceLength = minimumSequenceLength;
        this.maximumSequenceLength = maximumSequenceLength;
    }

    @Override
    public DataTable extract() {
        DataTable table = new DataTable();
        extract(table);
        return table;
    }

    @Override
    public void extract(DataTable table) {
        for (DumpInstance instance : instances) {
            if (instance.getSetType().equals(InstanceSetType.TRAIN_SET)) {
                Map<Sequence, Integer> sequences = getDumpSequences(instance.getInstance());
                for (Sequence subSequence : sequences.keySet()) {
                    table.put(instance, new SequenceFeatureKey(subSequence, 0), new FeatureValue<>(sequences.get(subSequence)));
                }
            }
        }
        for (DumpInstance instance : instances) {
            int instanceUniqueCounter = 0;
            int instanceBenignUniqueCounter = 0;
            FeatureKey<String, Integer> instanceUniqueFeatureKey = new FeatureKey<String, Integer>("SequenceTrainUnique");
            FeatureKey<String, Integer> instanceBenignUniqueFeatureKey = new FeatureKey<String, Integer>("SequenceTrainBenignUnique");
            if (instance.getSetType().equals(InstanceSetType.TRAIN_SET) && (trainDiffFeature | trainBenignDiffFeature)) {
                Map<Sequence, Integer> subSequences = getDumpSequences(instance.getInstance());
                for (Sequence subSequence : subSequences.keySet()) {
                    Feature feature = table.getFeature(new SequenceFeatureKey(subSequence, 0));
                    if (trainDiffFeature && feature.size() == 1) {
                        instanceUniqueCounter++;
                    }
                }
            }
            if (instance.getSetType().equals(InstanceSetType.TEST_SET)) {
                Map<Sequence, Integer> sequences = getDumpSequences(instance.getInstance());
                for (Sequence subSequence : sequences.keySet()) {
                    if (!table.putIfFeatureExists(instance, new SequenceFeatureKey(subSequence, 0), new FeatureValue<>(sequences.get(subSequence)))) {
                        instanceUniqueCounter++;
                    }
                }
            }
            if (trainDiffFeature) {
                table.put(instance, instanceUniqueFeatureKey, new FeatureValue<>(instanceUniqueCounter));
            }
            if (trainBenignDiffFeature) {
                table.put(instance, instanceBenignUniqueFeatureKey, new FeatureValue<>(instanceBenignUniqueCounter));
            }
        }

    }

    private Map<Sequence, Integer> getDumpSequences(Dump dump) {
        ISequenceFinder finder = new PrefixSpanSequenceFinder(minimumSupport, maximumSupport, minimumSequenceLength, maximumSequenceLength);
        List<List<Call>> sequences = new ArrayList<>();
        for (Process process : dump.getProcesses()) {
            for (Thread thread : process.getThreads()) {
                sequences.add(thread.getCallStack().getCallList());
            }
        }
        return finder.generateSubSequences(sequences);
    }

}
