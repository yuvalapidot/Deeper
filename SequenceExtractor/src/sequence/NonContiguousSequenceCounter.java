package sequence;

import model.data.DataTable;
import model.feature.FeatureValue;
import model.feature.SequenceFeatureKey;
import model.instance.DumpInstance;
import model.memory.*;
import model.memory.Process;
import model.memory.Thread;

import java.util.List;
import java.util.Set;

public class NonContiguousSequenceCounter implements ISequenceCounter {

    @Override
    public void countSequences(DataTable dataTable, List<DumpInstance> instances, Set<Sequence> sequences) {
        int sequenceCount;
        for (DumpInstance instance : instances) {
            for (Sequence sequence : sequences) {
                if ((sequenceCount = countSequenceInDump(instance.getInstance(), sequence)) != 0) {
                    dataTable.put(instance, new SequenceFeatureKey(sequence, 0), new FeatureValue<>(sequenceCount));
                }
            }
        }
    }

    private int countSequenceInDump(Dump dump, Sequence sequence) {
        int counter = 0;
        for (Process process : dump.getProcesses()) {
            for (Thread thread : process.getThreads()) {
                if (callStackContainsSequence(thread.getCallStack(), sequence)) {
                    counter++;
                }
            }
        }
        return counter;
    }

    private boolean callStackContainsSequence(CallStack stack, Sequence sequence) {
        List<Call> calls = stack.getCallList();
        if (calls.isEmpty()) {
            return false;
        }
        int pointer = 0;
        for (Call call : sequence.getCalls()) {
            while (!calls.get(pointer).equals(call)) {
                pointer++;
                if (pointer >= calls.size()) {
                    return false;
                }
            }
        }
        return true;
    }
}
