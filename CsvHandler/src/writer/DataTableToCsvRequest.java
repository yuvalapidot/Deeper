package writer;

import model.data.DataTable;

import java.util.Observable;

public class DataTableToCsvRequest extends Observable {

    private DataTable dataTable;
    private String csvPath;

    public DataTableToCsvRequest(DataTable dataTable, String csvPath) {
        this.dataTable = dataTable;
        this.csvPath = csvPath;
    }

    public DataTable getDataTable() {
        return dataTable;
    }

    public void setDataTable(DataTable dataTable) {
        this.dataTable = dataTable;
    }

    public String getCsvPath() {
        return csvPath;
    }

    public void setCsvPath(String csvPath) {
        this.csvPath = csvPath;
    }
}
