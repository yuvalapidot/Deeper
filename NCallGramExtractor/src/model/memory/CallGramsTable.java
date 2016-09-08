package model.memory;

import java.util.ArrayList;
import java.util.List;

public class CallGramsTable {

    private List<List<Call>> callGrams;
    private List<List<Integer>> ngrams;
    private List<Dump> dumps;

    public CallGramsTable(List<List<Call>> callGrams, List<List<Integer>> ngrams, List<Dump> dumps) {
        this.callGrams = callGrams;
        this.ngrams = ngrams;
        this.dumps = dumps;
    }

    public List<List<Call>> getCallGrams() {
        return callGrams;
    }

    public List<List<Integer>> getNgrams() {
        return ngrams;
    }

    public List<Dump> getDumps() {
        return dumps;
    }

    public List<List<?>> toGeneric() {
        List<List<?>> table = new ArrayList<>();
        table.add(callGrams);
        table.addAll(ngrams);
        return table;
    }
}
