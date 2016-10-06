package extractor;

import model.data.DataTable;
import model.feature.CallGramFeatureKey;
import model.feature.Feature;
import model.feature.FeatureKey;
import model.feature.FeatureValue;
import model.instance.DumpInstance;
import model.instance.InstanceSetType;
import model.memory.*;
import model.memory.Process;
import model.memory.Thread;

import java.util.LinkedHashMap;
import java.util.Map;

public class CallGramExtractor extends AbstractFeatureExtractor<DumpInstance> {

    private final int n;
    private boolean trainDiffFeature = true;
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
    public void extract(DataTable table) {
        for (DumpInstance instance : instances) {
            if (instance.getSetType().equals(InstanceSetType.TRAIN_SET)) {
                Map<CallGram, Integer> callGrams = getDumpCallGrams(instance.getInstance());
                for (CallGram callGram : callGrams.keySet()) {
                    table.put(instance, new CallGramFeatureKey(callGram, 0), new FeatureValue<>(callGrams.get(callGram)));
                }
            }
        }
        for (DumpInstance instance : instances) {
            int instanceUniqueCounter = 0;
            int instanceBenignUniqueCounter = 0;
            FeatureKey<String, Integer> instanceUniqueFeatureKey = new FeatureKey<String, Integer>(n + "GramTrainUnique");
            FeatureKey<String, Integer> instanceBenignUniqueFeatureKey = new FeatureKey<String, Integer>(n + "GramTrainBenignUnique");
            if (instance.getSetType().equals(InstanceSetType.TRAIN_SET) && (trainDiffFeature | trainBenignDiffFeature)) {
                Map<CallGram, Integer> callGrams = getDumpCallGrams(instance.getInstance());
                for (CallGram callGram : callGrams.keySet()) {
                    Feature feature = table.getFeature(new CallGramFeatureKey(callGram, 0));
                    if (trainDiffFeature && feature.size() == 1) {
                        instanceUniqueCounter++;
                    }
                }
            }
            if (instance.getSetType().equals(InstanceSetType.TEST_SET)) {
                Map<CallGram, Integer> callGrams = getDumpCallGrams(instance.getInstance());
                for (CallGram callGram : callGrams.keySet()) {
                    if (!table.putIfFeatureExists(instance, new CallGramFeatureKey(callGram, 0), new FeatureValue<>(callGrams.get(callGram)))) {
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

    private Map<CallGram, Integer> getDumpCallGrams(Dump dump) {
        Map<CallGram, Integer> callGrams = new LinkedHashMap<>();
        for (Process process : dump.getProcesses()) {
            for (Thread thread : process.getThreads()) {
                CallStack stack = thread.getCallStack();
                for (int i = 0; i <= stack.size() - n; i++) {
                    CallGram callGram = new CallGram(stack.getCallList().subList(i, i + n));
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
