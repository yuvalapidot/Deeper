package reader;

import java.io.File;
import java.util.Observable;

public class JsonToDumpRequest extends Observable {

    private String jsonPath;
    private File file;

    public JsonToDumpRequest(String jsonPath) {
        this.jsonPath = jsonPath;
    }

    public JsonToDumpRequest(File file) {
        this.file = file;
    }

    public String getJsonPath() {
        return (file == null) ? jsonPath : file.getPath();
    }

    public void setJsonPath(String jsonPath) {
        this.jsonPath = jsonPath;
    }

    public File getFile() {
        return (file == null) ? new File(jsonPath) : file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
