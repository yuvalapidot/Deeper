package extractor;

import model.instance.Instance;

import java.util.List;
import java.util.Map;

public abstract class AbstractFeatureExtractor<T extends Instance> implements IFeatureExtractor<T> {

    protected List<T> instances;
    protected Map<? extends Object, List<T>> instanceMap;

    @Override
    public void setInstances(List<T> instances) {
        this.instances = instances;
    }

    @Override
    public void setInstances(Map<? extends Object, List<T>> instances) {
        this.instanceMap = instances;
    }
}
