package model.instance;

import model.memory.Dump;

public class DumpInstance extends Instance<Dump> {

    public DumpInstance(Dump instance, InstanceSetType setType, String classification) {
        super(instance, setType, classification);
    }

    public DumpInstance(Dump instance, InstanceSetType setType) {
        super(instance, setType);
    }

    public DumpInstance(Dump dump) {
        super(dump);
    }

    public static DumpInstance instance(Dump dump) {
        DumpInstance instance = new DumpInstance(dump);
        return (DumpInstance) instance(instance);
    }

    public static DumpInstance instance(Dump dump, InstanceSetType setType) {
        DumpInstance instance = new DumpInstance(dump, setType);
        return (DumpInstance) instance(instance);
    }

    public static DumpInstance instance(Dump dump, InstanceSetType setType, String classification) {
        DumpInstance instance = new DumpInstance(dump, setType, classification);
        return (DumpInstance) instance(instance);
    }

    @Override
    public String getName() {
        return instance.getName();
    }


}
