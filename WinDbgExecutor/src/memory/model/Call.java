package memory.model;

import java.util.Arrays;

public class Call {

    public static final int ARGS_COUNT = 4;
    private String childSp;
    private String returnAddress;
    private String[] args;
    private String callSite;

    public Call() {
        args = new String[ARGS_COUNT];
    }

    public String getChildSp() {
        return childSp;
    }

    public void setChildSp(String childSp) {
        this.childSp = childSp;
    }

    public String getReturnAddress() {
        return returnAddress;
    }

    public void setReturnAddress(String returnAddress) {
        this.returnAddress = returnAddress;
    }

    public String getArg(int index) {
        return args[index];
    }

    public void setArg(String arg, int index) {
        args[index] = arg;
    }

    public String getCallSite() {
        return callSite;
    }

    public void setCallSite(String callSite) {
        this.callSite = callSite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Call call = (Call) o;

        if (childSp != null ? !childSp.equals(call.childSp) : call.childSp != null) return false;
        if (returnAddress != null ? !returnAddress.equals(call.returnAddress) : call.returnAddress != null)
            return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(args, call.args)) return false;
        return callSite != null ? callSite.equals(call.callSite) : call.callSite == null;

    }

    @Override
    public int hashCode() {
        int result = childSp != null ? childSp.hashCode() : 0;
        result = 31 * result + (returnAddress != null ? returnAddress.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(args);
        result = 31 * result + (callSite != null ? callSite.hashCode() : 0);
        return result;
    }
}
