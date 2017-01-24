package writer;

import model.data.DataTable;
import model.instance.InstanceSetType;

import java.util.Observable;
import java.util.Set;

public class DataTableToCsvRequest extends Observable {

    private DataTable dataTable;
    private String csvPath;
    private CsvNumberRepresentation representation = CsvNumberRepresentation.INTEGER_REPRESENTATION;
    private Set<InstanceSetType> instanceSetTypesFilter;
    private int featurePercentage;
    private double scoreThreshold;

    public DataTableToCsvRequest(DataTable dataTable, String csvPath, CsvNumberRepresentation representation, Set<InstanceSetType> instanceSetTypes, int featurePercentage, double scoreThreshold) {
        this.dataTable = dataTable;
        this.csvPath = csvPath;
        this.representation = representation;
        this.instanceSetTypesFilter = instanceSetTypes;
        this.featurePercentage = featurePercentage;
        this.scoreThreshold = scoreThreshold;
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

    public Set<InstanceSetType> getInstanceSetTypesFilter() {
        return instanceSetTypesFilter;
    }

    public void setInstanceSetTypesFilter(Set<InstanceSetType> instanceSetTypesFilter) {
        this.instanceSetTypesFilter = instanceSetTypesFilter;
    }

    public int getFeaturePercentage() {
        return featurePercentage;
    }

    public void setFeaturePercentage(int featurePercentage) {
        this.featurePercentage = featurePercentage;
    }

    public double getScoreThreshold() {
        return scoreThreshold;
    }

    public void setScoreThreshold(double scoreThreshold) {
        this.scoreThreshold = scoreThreshold;
    }
}
