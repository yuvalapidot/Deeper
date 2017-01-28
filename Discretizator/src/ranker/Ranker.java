package ranker;

import model.data.DataTable;
import model.feature.Feature;
import model.feature.RankedFeature;
import model.instance.Instance;
import ranker.rankers.IRankerMethod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class Ranker {

    private IRankerMethod rankerMethod;

    public Ranker(IRankerMethod rankerMethod) {
        this.rankerMethod = rankerMethod;
    }

    public DataTable rankTable(DataTable table) {
        DataTable rankedTable = new DataTable();
        List<RankedFeature> rankedFeatures = new ArrayList<>();
        Feature classFeature = null;
        for (Feature feature : table.getFeatures()) {
            if (feature.getKey().equals("Class")) {
                classFeature = feature;
            } else {
                RankedFeature rankedFeature = rank(feature, table.getInstances());
                rankedFeature.setDataTable(rankedTable);
                rankedFeatures.add(rankedFeature);
            }
        }
        rankedFeatures.sort(RankedFeature::compareTo);
        Collections.reverse(rankedFeatures);
        for (RankedFeature rankedFeature : rankedFeatures) {
            rankedTable.put(rankedFeature);
        }
        rankedTable.put(classFeature);
        return rankedTable;
    }

    private RankedFeature rank(Feature feature, Set<Instance> instances) {
        return new RankedFeature(feature, rankerMethod.rank(feature, instances));
    }
}
