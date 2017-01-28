package model.memory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sequence {

    private List<Call> calls;

    private static Map<Sequence, Sequence> lookupTable = new HashMap<>();

    private Sequence() {
        calls = new ArrayList<>();
    }

    private Sequence(Call call) {
        this();
        calls.add(Call.instance(call));
    }

    private Sequence(Sequence sequence) {
        this();
        calls.addAll(sequence.calls);
    }

    private Sequence(List<Call> calls) {
        this();
        for (Call call : calls) {
            this.calls.add(Call.instance(call));
        }
    }

    private Sequence(Sequence sequence, Call call) {
        this(sequence);
        calls.add(Call.instance(call));
    }

    public static Sequence instance(Sequence sequence) {
        Sequence existingSequence = lookupTable.putIfAbsent(sequence, sequence);
        return existingSequence == null ? sequence : existingSequence;
    }

    public static Sequence instance() {
        Sequence sequence = new Sequence();
        return instance(sequence);
    }

    public static Sequence instance(Call call) {
        Sequence sequence = new Sequence(call);
        return instance(sequence);
    }

    public static Sequence instance(List<Call> calls) {
        Sequence sequence = new Sequence(calls);
        return instance(sequence);
    }

    public static Sequence instance(Sequence sequence, Call call) {
        Sequence newSequence = new Sequence(sequence, call);
        return instance(newSequence);
    }

    public void addCall(Call call) {
        calls.add(call);
    }

    public boolean removeCall(Call call) {
        return calls.remove(call);
    }

    public Call removeCall(int index) {
        return calls.remove(index);
    }

    public List<Call> getCalls() {
        return calls;
    }

    public int size() {
        return calls.size();
    }

    public Sequence subSequence(int from, int to) {
        return new Sequence(calls.subList(from, to));
    }

    @Override
    public String toString() {
        return size() + "Sequence{" + calls + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sequence)) return false;

        Sequence callGram = (Sequence) o;

        return calls.equals(callGram.calls);

    }

    @Override
    public int hashCode() {
        return calls.hashCode();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (lookupTable.get(this) == this) {
            lookupTable.remove(this);
        }
    }
}
