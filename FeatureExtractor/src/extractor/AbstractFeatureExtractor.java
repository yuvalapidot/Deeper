package extractor;

import java.util.List;

public abstract class AbstractFeatureExtractor<T> implements IFeatureExtractor<T> {

    protected List<T> instances;

    @Override
    public void setInstances(List<T> instances) {
        this.instances = instances;
    }
}
