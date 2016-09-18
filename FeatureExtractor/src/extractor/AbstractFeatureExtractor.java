package extractor;

import model.instance.Instance;

import java.util.List;

public abstract class AbstractFeatureExtractor<T extends Instance> implements IFeatureExtractor<T> {

    protected List<T> instances;

    @Override
    public void setInstances(List<T> instances) {
        this.instances = instances;
    }
}
