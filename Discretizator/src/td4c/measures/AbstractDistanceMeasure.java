package td4c.measures;

import exceptions.DataUnsupervisedException;
import model.data.DataTable;
import model.feature.Feature;
import model.feature.FeatureKey;

abstract class AbstractDistanceMeasure implements IDistanceMeasure {

    void checkSupervised(DataTable table) {
        if (table.getFeature(new FeatureKey("Class")) == null) {
            throw new DataUnsupervisedException("Class feature was not found in data - " +
                    "data is considered unsupervised, TD4C method requires supervised data.");
        }
    }

    Feature getClass(DataTable table) {
        return table.getFeature(new FeatureKey("Class"));
    }
}
