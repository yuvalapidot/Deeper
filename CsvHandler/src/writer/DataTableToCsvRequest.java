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
    private int minimumNumberOfFeatures;
    private int maximumNumberOfFeatures;

    public DataTableToCsvRequest(DataTable dataTable, String csvPath, CsvNumberRepresentation representation, Set<InstanceSetType> instanceSetTypes, int featurePercentage, int minimumNumberOfFeatures, int maximumNumberOfFeatures) {
        this.dataTable = dataTable;
        this.csvPath = csvPath;
        this.representation = representation;
        this.instanceSetTypesFilter = instanceSetTypes;
        this.featurePercentage = featurePercentage;
        this.minimumNumberOfFeatures = minimumNumberOfFeatures;
        this.maximumNumberOfFeatures = maximumNumberOfFeatures;
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

    public int getMinimumNumberOfFeatures() {
        return minimumNumberOfFeatures;
    }

    public void setMinimumNumberOfFeatures(int minimumNumberOfFeatures) {
        this.minimumNumberOfFeatures = minimumNumberOfFeatures;
    }

    public int getMaximumNumberOfFeatures() {
        return maximumNumberOfFeatures;
    }

    public void setMaximumNumberOfFeatures(int maximumNumberOfFeatures) {
        this.maximumNumberOfFeatures = maximumNumberOfFeatures;
    }
}
