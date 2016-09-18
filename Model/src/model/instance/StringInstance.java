package model.instance;

public class StringInstance extends Instance<String> {

    public StringInstance(String instance, InstanceSetType setType) {
        super(instance, setType);
    }

    public StringInstance(String dump) {
        super(dump);
    }

    @Override
    public String getName() {
        return instance;
    }
}
