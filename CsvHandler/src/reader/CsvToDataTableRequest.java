package reader;

import java.util.Observable;

public class CsvToDataTableRequest extends Observable {

    boolean namesOnFirstColumn;
    private String csvPath;

    public CsvToDataTableRequest(String csvPath, boolean namesOnFirstColumn) {
        this.namesOnFirstColumn = namesOnFirstColumn;
        this.csvPath = csvPath;
    }

    public String getCsvPath() {
        return csvPath;
    }

    public void setCsvPath(String csvPath) {
        this.csvPath = csvPath;
    }

    public boolean isNamesOnFirstColumn() {
        return namesOnFirstColumn;
    }

    public void setNamesOnFirstColumn(boolean namesOnFirstColumn) {
        this.namesOnFirstColumn = namesOnFirstColumn;
    }
}
