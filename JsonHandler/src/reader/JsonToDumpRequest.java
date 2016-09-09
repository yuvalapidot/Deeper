package reader;

import java.util.Observable;

public class JsonToDumpRequest extends Observable {

    private String jsonPath;

    public JsonToDumpRequest(String jsonPath) {
        this.jsonPath = jsonPath;
    }

    public String getJsonPath() {
        return jsonPath;
    }

    public void setJsonPath(String jsonPath) {
        this.jsonPath = jsonPath;
    }
}
