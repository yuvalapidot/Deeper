package model;

import model.memory.Call;

import java.util.Iterator;
import java.util.List;

public class PseudoCallList implements Iterable<Call> {

    private int pointer;
    private List<Call> calls;

    public PseudoCallList(List<Call> calls) {
        pointer = 0;
        this.calls = calls;
    }

    public PseudoCallList(List<Call> calls, int pointer) {
        this.pointer = pointer;
        this.calls = calls;
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
