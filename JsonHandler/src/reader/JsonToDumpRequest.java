package reader;

/**
 * Created by yuvalla on 07/09/2016.
 */
public class JsonToDumpRequest {

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
