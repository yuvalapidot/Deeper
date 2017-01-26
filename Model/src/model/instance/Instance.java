package model.instance;

public abstract class Instance<T> {

    protected final T instance;
    protected InstanceSetType setType = InstanceSetType.TRAIN_SET;
    protected String classification = "Unknown";
    protected String type = "UNKNOWN";
    protected int timestamp = 0;

    public Instance(T instance, InstanceSetType setType, String classification) {
        this.instance = instance;
        this.setType = setType;
        this.classification = classification;
    }

    public Instance(T instance, InstanceSetType setType) {
        this.instance = instance;
        this.setType = setType;
    }

    public Instance(T instance) {
        this.instance = instance;
    }

    public T getInstance() {
        return instance;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public abstract String getName();

    public InstanceSetType getSetType() {
        return setType;
    }

    public void setSetType(InstanceSetType setType) {
        this.setType = setType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Instance)) return false;

        Instance<?> instance1 = (Instance<?>) o;

        if (!instance.equals(instance1.instance)) return false;
        return setType == instance1.setType;

    }

    @Override
    public int hashCode() {
        int result = instance.hashCode();
        result = 31 * result + setType.hashCode();
        return result;
    }
}
