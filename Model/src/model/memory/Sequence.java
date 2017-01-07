package model.memory;

import java.util.ArrayList;
import java.util.List;

public class Sequence {

    private List<Call> calls;

    public Sequence() {
        calls = new ArrayList<>();
    }

    public Sequence(Call call) {
        this();
        calls.add(call);
    }

    public Sequence(Sequence sequence) {
        this();
        calls.addAll(sequence.calls);
    }

    public Sequence(List<Call> calls) {
        this.calls = calls;
    }

    public Sequence(Sequence sequence, Call call) {
        this(sequence);
        calls.add(call);
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
}
