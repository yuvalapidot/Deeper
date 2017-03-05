package extractor;

import model.data.DataTable;
import model.feature.SequenceFeatureKey;
import model.feature.Feature;
import model.instance.DumpInstance;
import model.instance.InstanceSetType;
import model.memory.*;
import model.memory.Process;
import model.memory.Thread;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.LinkedHashMap;
import java.util.Map;

public class CallGramExtractor extends AbstractFeatureExtractor<DumpInstance> {

    private final int n;
    private boolean trainDiffFeature = false;
    private boolean trainBenignDiffFeature = false;

    public CallGramExtractor(int n) {
        this.n = n;
    }

    @Override
    public DataTable extract() {
        DataTable table = new DataTable();
        extract(table);
        return table;
    }

    @Override
    public void extractToDataBase() {
        throw new NotImplementedException();
    }

    @Override
    public void extract(DataTable table) {
        for (DumpInstance instance : instances) {
            if (instance.getSetType().equals(InstanceSetType.TRAIN_SET)) {
                Map<Sequence, Integer> callGrams = getDumpCallGrams(instance.getInstance());
                for (Sequence callGram : callGrams.keySet()) {
                    table.put(instance, callGram, callGrams.get(callGram));
                }
            }
        }
        for (DumpInstance instance : instances) {
            int instanceUniqueCounter = 0;
            int instanceBenignUniqueCounter = 0;
            String instanceUniqueFeatureKey = n + "GramTrainUnique";
            String instanceBenignUniqueFeatureKey = n + "GramTrainBenignUnique";
            if (instance.getSetType().equals(InstanceSetType.TRAIN_SET) && (trainDiffFeature | trainBenignDiffFeature)) {
                Map<Sequence, Integer> callGrams = getDumpCallGrams(instance.getInstance());
                for (Sequence callGram : callGrams.keySet()) {
                    Feature feature = table.getFeature(new SequenceFeatureKey(callGram, 0));
                    if (trainDiffFeature && feature.size() == 1) {
                        instanceUniqueCounter++;
                    }
                }
            }
            if (instance.getSetType().equals(InstanceSetType.TEST_SET)) {
                Map<Sequence, Integer> callGrams = getDumpCallGrams(instance.getInstance());
                for (Sequence callGram : callGrams.keySet()) {
                    if (!table.putIfFeatureExists(instance, callGram, callGrams.get(callGram))) {
                        instanceUniqueCounter++;
                    }
                }
            }
            if (trainDiffFeature) {
                table.put(instance, instanceUniqueFeatureKey, instanceUniqueCounter);
            }
            if (trainBenignDiffFeature) {
                table.put(instance, instanceBenignUniqueFeatureKey, instanceBenignUniqueCounter);
            }
        }
    }

    private Map<Sequence, Integer> getDumpCallGrams(Dump dump) {
        Map<Sequence, Integer> callGrams = new LinkedHashMap<>();
        for (Process process : dump.getProcesses()) {
            for (Thread thread : process.getThreads()) {
                CallStack stack = thread.getCallStack();
                for (int i = 0; i <= stack.size() - n; i++) {
                    Sequence callGram = Sequence.instance(stack.getCallList().subList(i, i + n));
                    Integer value;
                    if ((value = callGrams.putIfAbsent(callGram, 1)) != null) {
                        callGrams.put(callGram, value + 1);
                    }
                }
            }
        }
        return callGrams;
    }
}
