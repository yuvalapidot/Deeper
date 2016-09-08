package model.memory;

public class Thread {

    private final String id;
    private String cid;
    private String teb;
    private String win32Thread;
    private CallStack callStack;

    public Thread(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getTeb() {
        return teb;
    }

    public void setTeb(String teb) {
        this.teb = teb;
    }

    public String getWin32Thread() {
        return win32Thread;
    }

    public void setWin32Thread(String win32Thread) {
        this.win32Thread = win32Thread;
    }

    public CallStack getCallStack() {
        return callStack;
    }

    public void setCallStack(CallStack callStack) {
        this.callStack = callStack;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Thread thread = (Thread) o;

        if (id != null ? !id.equals(thread.id) : thread.id != null) return false;
        if (cid != null ? !cid.equals(thread.cid) : thread.cid != null) return false;
        if (teb != null ? !teb.equals(thread.teb) : thread.teb != null) return false;
        if (win32Thread != null ? !win32Thread.equals(thread.win32Thread) : thread.win32Thread != null) return false;
        return callStack != null ? callStack.equals(thread.callStack) : thread.callStack == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (cid != null ? cid.hashCode() : 0);
        result = 31 * result + (teb != null ? teb.hashCode() : 0);
        result = 31 * result + (win32Thread != null ? win32Thread.hashCode() : 0);
        result = 31 * result + (callStack != null ? callStack.hashCode() : 0);
        return result;
    }
}
