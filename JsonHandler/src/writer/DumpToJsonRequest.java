package writer;

import java.util.Observable;

public class DumpToJsonRequest extends Observable {

    private String dumpPath;
    private String jsonPath;
    private boolean skipIfExists = false;

    public DumpToJsonRequest(String dumpPath, String jsonPath, boolean skipIfExists) {
        this.dumpPath = dumpPath;
        this.jsonPath = jsonPath;
        this.skipIfExists = skipIfExists;
    }

    public DumpToJsonRequest(String dumpPath, String jsonPath) {
        this.dumpPath = dumpPath;
        this.jsonPath = jsonPath;
    }

    public String getDumpPath() {
        return dumpPath;
    }

    public void setDumpPath(String dumpPath) {
        this.dumpPath = dumpPath;
    }

    public String getJsonPath() {
        return jsonPath;
    }

    public void setJsonPath(String jsonPath) {
        this.jsonPath = jsonPath;
    }

    public boolean isSkipIfExists() {
        return skipIfExists;
    }

    public void setSkipIfExists(boolean skipIfExists) {
        this.skipIfExists = skipIfExists;
    }

}
