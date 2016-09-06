package creator;

import memory.model.*;
import memory.model.Process;
import memory.model.Thread;

import java.util.ArrayList;
import java.util.List;

public class SystemCallGramCreator {

    public CallGramsTable generateNGrams(List<Dump> dumps, int n) {
        List<List<Call>> grams = new ArrayList<>();
        List<List<Integer>> ngrams = new ArrayList<>();
        for (Dump dump : dumps) {
            List<Integer> dumpGrams = new ArrayList<>(grams.size());
            for (Process process : dump.getProcesses()) {
                for (Thread thread : process.getThreads()) {
                    CallStack stack = thread.getCallStack();
                    for (int i = 0; i <= stack.size() - n; i++) {
                        List<Call> callGram = stack.getCallList().subList(i, i + n);
                        int gramIndex = grams.indexOf(callGram);
                        if (gramIndex < 0) {
                            gramIndex = grams.size();
                            grams.add(callGram);
                        }
                        while (gramIndex >= dumpGrams.size()) {
                            dumpGrams.add(0);
                        }
                        dumpGrams.set(gramIndex, dumpGrams.get(gramIndex) + 1);
                    }
                }
            }
            ngrams.add(dumpGrams);
        }
        for (List<Integer> dumpGrams : ngrams) {
            for (int i = 0; i < grams.size(); i++) {
                if (i >= dumpGrams.size() || dumpGrams.get(i) == null) {
                    dumpGrams.add(i, 0);
                }
            }
        }
        return new CallGramsTable(grams, ngrams, dumps);
    }

    public CallGramsTable generateUpToNGrams(List<Dump> dumps, int from, int to) {
        CallGramsTable table = generateNGrams(dumps, to);
        for (int i = to - 1; i >= from; i--) {
            table = concatNGramTables(generateNGrams(dumps, i), table);
        }
        return table;
    }

    public CallGramsTable generateUpToNGrams(List<Dump> dumps, int to) {
        return generateUpToNGrams(dumps, 1, to);
    }

    private CallGramsTable concatNGramTables(CallGramsTable first, CallGramsTable second) {
        List<List<Call>> grams = new ArrayList<>(first.getCallGrams());
        grams.addAll(second.getCallGrams());
        List<List<Integer>> ngrams = new ArrayList<>();
        for (int i = 0; i < Math.min(first.getNgrams().size(), second.getNgrams().size()); i++) {
            List<Integer> dumpGram = new ArrayList<>(first.getNgrams().get(i));
            dumpGram.addAll(second.getNgrams().get(i));
            ngrams.add(dumpGram);
        }
        return new CallGramsTable(grams, ngrams, first.getDumps());
    }
}
