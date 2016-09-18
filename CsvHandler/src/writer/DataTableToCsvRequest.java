package writer;

import model.data.DataTable;

import java.util.Observable;

public class DataTableToCsvRequest extends Observable {

    private DataTable dataTable;
    private String csvPath;
    private CsvNumberRepresentation representation = CsvNumberRepresentation.INTEGER_REPRESENTATION;

    public DataTableToCsvRequest(DataTable dataTable, String csvPath, CsvNumberRepresentation representation) {
        this.dataTable = dataTable;
        this.csvPath = csvPath;
        this.representation = representation;
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

    public CsvNumberRepresentation getRepresentation() {
        return representation;
    }

    public void setRepresentation(CsvNumberRepresentation representation) {
        this.representation = representation;
    }
}
