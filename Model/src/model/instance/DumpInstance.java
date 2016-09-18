package model.instance;

import model.memory.Dump;

public class DumpInstance extends Instance<Dump> {

    public DumpInstance(Dump instance, InstanceSetType setType) {
        super(instance, setType);
    }

    public DumpInstance(Dump dump) {
        super(dump);
    }

    @Override
    public String getName() {
        return instance.getName();
    }
}
