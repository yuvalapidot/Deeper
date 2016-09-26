package model.instance;

public abstract class Instance<T> {

    protected final T instance;
    private InstanceSetType setType = InstanceSetType.TRAIN_SET;

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

    public abstract String getName();

    public InstanceSetType getSetType() {
        return setType;
    }

    public void setSetType(InstanceSetType setType) {
        this.setType = setType;
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
