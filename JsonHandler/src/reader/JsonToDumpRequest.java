package reader;

import java.util.Observable;

/**
 * Created by yuvalla on 07/09/2016.
 */
public class JsonToDumpRequest extends Observable {

    String jsonPath;

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
