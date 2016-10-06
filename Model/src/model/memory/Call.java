package model.memory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Call {

    public static final int ARGS_COUNT = 4;
    private static final boolean CASE_SENSITIVE = false;

    private String childSp;
    private String returnAddress;
    private String[] args;
    private String callSite;

    public Call(String childSp, String returnAddress, String[] args, String callSite) {
        this.childSp = childSp;
        this.returnAddress = returnAddress;
        this.args = args;
        this.callSite = callSite;
    }

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

    public String getShortName() {
        try {
            String callSiteName = callSite.substring(0, callSite.indexOf("+"));
            return CASE_SENSITIVE ? callSiteName : callSiteName.toLowerCase();
        } catch (Exception e) {
            return CASE_SENSITIVE ? callSite : callSite.toLowerCase();
        }
    }
}
