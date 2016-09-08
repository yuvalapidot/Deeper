package model.feature;

public class Instance {

    private final String name;

    public Instance(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Instance)) return false;

        Instance instance = (Instance) o;

        return getName().equals(instance.getName());

    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }
}
