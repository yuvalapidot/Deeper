package ranker;

import model.data.DataTable;
import model.feature.Feature;
import model.feature.RankedFeature;
import model.instance.Instance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ranker.rankers.IRankerMethod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class Ranker {

    private IRankerMethod rankerMethod;

    private static final Logger log = LogManager.getLogger(Ranker.class);

    public Ranker(IRankerMethod rankerMethod) {
        this.rankerMethod = rankerMethod;
    }

    public DataTable rankTable(DataTable table, double threshold) {
        DataTable rankedTable = new DataTable();
        List<RankedFeature> rankedFeatures = new ArrayList<>();
        Feature classFeature = null;
        int rankedFeatureCount = 0;
        for (Feature feature : table.getFeatures()) {
            if (feature.getKey().equals("Class")) {
                classFeature = feature;
            } else {
                RankedFeature rankedFeature = rank(feature, table.getInstances());
                if (rankedFeature.getRank() > threshold) {
                    rankedFeature.setDataTable(rankedTable);
                    rankedFeatures.add(rankedFeature);
                    rankedFeatureCount++;
                }
            }
        }
        log.info(rankedFeatureCount + " features out of " + table.getFeatures().size() + " were ranked above threshold (" + threshold + ")");
        rankedFeatures.sort(RankedFeature::compareTo);
        Collections.reverse(rankedFeatures);
        int numberOfInstances = table.getInstances().size();
        int addedFeatureCount = 0;
        for (RankedFeature rankedFeature : rankedFeatures) {
            boolean toAdd = true;
            for (Feature existingFeature : rankedTable.getFeatures()) {
                if (existingFeature.correlationRatio(rankedFeature) > ((numberOfInstances - 1) / (double) numberOfInstances)) {
                    toAdd = false;
                    break;
                }
            }
            if (toAdd) {
                rankedTable.put(rankedFeature);
                addedFeatureCount++;
            }
        }
        log.info(addedFeatureCount + " Non correlated features out of " + rankedFeatureCount + " ranked features were added to ranked data table");
        rankedTable.put(classFeature);
        return rankedTable;
    }

    private RankedFeature rank(Feature feature, Set<Instance> instances) {
        return new RankedFeature(feature, rankerMethod.rank(feature, instances));
    }
}
