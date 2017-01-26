package ranker.rankers;

import model.feature.Feature;
import model.instance.Instance;

import java.util.Set;

public interface IRankerMethod {

    public double rank(Feature<Integer> feature, Set<Instance> instances);
}
