package model.memory;

import java.util.ArrayList;
import java.util.List;

public class CallGram {

    private List<Call> calls;

    public CallGram() {
        calls = new ArrayList<>();
    }

    public CallGram(List<Call> calls) {
        this.calls = calls;
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

    public int size() {
        return calls.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CallGram)) return false;

        CallGram callGram = (CallGram) o;

        return calls.equals(callGram.calls);

    }

    @Override
    public int hashCode() {
        return calls.hashCode();
    }
}
