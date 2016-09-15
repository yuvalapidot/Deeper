package model.memory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Process {

    private String id;
    private String sessionId;
    private String cid;
    private String peb;
    private String image;
    private String parentCid;
    private List<Thread> threads;

    public Process() {
    }

    public Process(String id, String sessionId, String cid, String peb, String image, String parentCid, List<Thread> threads) {
        this.id = id;
        this.sessionId = sessionId;
        this.cid = cid;
        this.peb = peb;
        this.image = image;
        this.parentCid = parentCid;
        this.threads = threads;
    }

    public Process(String id) {
        this.id = id;
        threads = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getPeb() {
        return peb;
    }

    public void setPeb(String peb) {
        this.peb = peb;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getParentCid() {
        return parentCid;
    }

    public void setParentCid(String parentCid) {
        this.parentCid = parentCid;
    }

    public List<Thread> getThreads() {
        return threads;
    }

    public void addThread(Thread thread) {
        this.threads.add(thread);
    }

    public void addThreads(Collection<Thread> threads) {
        this.threads.addAll(threads);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Process process = (Process) o;

        if (id != null ? !id.equals(process.id) : process.id != null) return false;
        if (sessionId != null ? !sessionId.equals(process.sessionId) : process.sessionId != null) return false;
        if (cid != null ? !cid.equals(process.cid) : process.cid != null) return false;
        if (peb != null ? !peb.equals(process.peb) : process.peb != null) return false;
        if (image != null ? !image.equals(process.image) : process.image != null) return false;
        if (parentCid != null ? !parentCid.equals(process.parentCid) : process.parentCid != null) return false;
        return threads != null ? threads.equals(process.threads) : process.threads == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (sessionId != null ? sessionId.hashCode() : 0);
        result = 31 * result + (cid != null ? cid.hashCode() : 0);
        result = 31 * result + (peb != null ? peb.hashCode() : 0);
        result = 31 * result + (image != null ? image.hashCode() : 0);
        result = 31 * result + (parentCid != null ? parentCid.hashCode() : 0);
        result = 31 * result + (threads != null ? threads.hashCode() : 0);
        return result;
    }
}
