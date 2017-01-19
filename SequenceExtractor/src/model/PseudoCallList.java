package model;

import model.instance.DumpInstance;
import model.memory.Call;

import java.util.Iterator;
import java.util.List;

public class PseudoCallList implements Iterable<Call> {

    private int pointer;
    private List<Call> calls;
    private DumpInstance belongsTo;

    public PseudoCallList(DumpInstance belongsTo, List<Call> calls) {
        this.belongsTo = belongsTo;
        pointer = 0;
        this.calls = calls;
    }

    public PseudoCallList(DumpInstance belongsTo, List<Call> calls, int pointer) {
        this.belongsTo = belongsTo;
        this.pointer = pointer;
        this.calls = calls;
    }

    public DumpInstance getBelongsTo() {
        return belongsTo;
    }

    public int getPointer() {
        return pointer;
    }

    public List<Call> getCalls() {
        return calls;
    }

    public int size() {
        return calls.size() - pointer;
    }

    @Override
    public Iterator<Call> iterator() {
        Iterator iterator = calls.iterator();
        for (int i = 0; i < pointer; i++) {
            iterator.next();
        }
        return iterator;
    }
}
