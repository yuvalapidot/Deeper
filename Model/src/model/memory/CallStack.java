package model.memory;

import java.util.ArrayList;
import java.util.List;

public class CallStack {

    private List<Call> callList;

    public CallStack(List<Call> callList) {
        this.callList = callList;
    }

    public CallStack() {
        callList = new ArrayList<>();
    }

    public void appendCall(Call call) {
        callList.add(call);
    }

    public void prependCall(Call call) {
        callList.add(0, call);
    }

    public List<Call> getCallList() {
        return callList;
    }

    public int size() {
        return callList.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CallStack callStack = (CallStack) o;

        return callList != null ? callList.equals(callStack.callList) : callStack.callList == null;

    }

    @Override
    public int hashCode() {
        return callList != null ? callList.hashCode() : 0;
    }
}
