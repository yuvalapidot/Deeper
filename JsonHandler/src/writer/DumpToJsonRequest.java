package writer;

public class DumpToJsonRequest {

    private String dumpPath;
    private String jsonPath;
    private String dumpClassification = "Unknown";
    private boolean skipIfExists = false;

    public DumpToJsonRequest(String dumpPath, String jsonPath, String dumpClassification, boolean skipIfExists) {
        this.dumpPath = dumpPath;
        this.jsonPath = jsonPath;
        this.dumpClassification = dumpClassification;
        this.skipIfExists = skipIfExists;
    }

    public DumpToJsonRequest(String dumpPath, String jsonPath, String dumpClassification) {
        this.dumpPath = dumpPath;
        this.jsonPath = jsonPath;
        this.dumpClassification = dumpClassification;
    }

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

    public String getDumpClassification() {
        return dumpClassification;
    }

    public void setDumpClassification(String dumpClassification) {
        this.dumpClassification = dumpClassification;
    }

    public boolean isSkipIfExists() {
        return skipIfExists;
    }

    public void setSkipIfExists(boolean skipIfExists) {
        this.skipIfExists = skipIfExists;
    }
}
