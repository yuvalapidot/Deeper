package memory.model;

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

        return callSite != null ? getShortName().equals(call.getShortName()) : call.callSite == null;

    }

    @Override
    public int hashCode() {
        return callSite != null ? getShortName().hashCode() : 0;
    }

    @Override
    public String toString() {
        return getShortName();
    }

    private String getShortName() {
        try {
            return callSite.substring(0, callSite.indexOf("+"));
        } catch (Exception e) {
            return callSite;
        }
    }
}
