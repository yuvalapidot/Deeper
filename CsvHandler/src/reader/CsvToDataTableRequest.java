package reader;

import java.util.Observable;

public class CsvToDataTableRequest extends Observable {

    private String csvPath;

    public CsvToDataTableRequest(String csvPath) {
        this.csvPath = csvPath;
    }

    public String getCsvPath() {
        return csvPath;
    }

    public void setCsvPath(String csvPath) {
        this.csvPath = csvPath;
    }
}
