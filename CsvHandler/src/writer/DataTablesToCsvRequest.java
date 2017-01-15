package writer;

import model.data.DataTable;
import model.instance.InstanceSetType;

import java.util.Observable;
import java.util.Set;

public class DataTablesToCsvRequest extends Observable {

    private DataTable[] dataTable;
    private String csvPath;
    private CsvNumberRepresentation[] representation = { CsvNumberRepresentation.INTEGER_REPRESENTATION };
    private Set<InstanceSetType> instanceSetTypesFilter;

    public DataTablesToCsvRequest(DataTable[] dataTable, String csvPath, CsvNumberRepresentation[] representation, Set<InstanceSetType> instanceSetTypes) {
        this.dataTable = dataTable;
        this.csvPath = csvPath;
        this.representation = representation;
        this.instanceSetTypesFilter = instanceSetTypes;
    }

    public DataTable[] getDataTable() {
        return dataTable;
    }

    public void setDataTable(DataTable[] dataTable) {
        this.dataTable = dataTable;
    }

    public String getCsvPath() {
        return csvPath;
    }

    public void setCsvPath(String csvPath) {
        this.csvPath = csvPath;
    }

    public CsvNumberRepresentation[] getRepresentation() {
        return representation;
    }

    public void setRepresentation(CsvNumberRepresentation[] representation) {
        this.representation = representation;
    }

    public Set<InstanceSetType> getInstanceSetTypesFilter() {
        return instanceSetTypesFilter;
    }

    public void setInstanceSetTypesFilter(Set<InstanceSetType> instanceSetTypesFilter) {
        this.instanceSetTypesFilter = instanceSetTypesFilter;
    }
}