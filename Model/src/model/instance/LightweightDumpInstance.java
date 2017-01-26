package model.instance;

import model.memory.Dump;

public class LightweightDumpInstance extends Instance<Dump> {

    private String name;

    public LightweightDumpInstance(Dump instance, InstanceSetType setType, String classification) {
        super(null, setType, classification);
        name = instance.getName();
    }

    public LightweightDumpInstance(Dump instance, InstanceSetType setType) {
        super(null, setType);
        name = instance.getName();
    }

    public LightweightDumpInstance(Dump dump) {
        super(null);
        name = instance.getName();
    }

    public LightweightDumpInstance(DumpInstance dumpInstance) {
        super(null, dumpInstance.getSetType(), dumpInstance.getClassification());
        this.timestamp = dumpInstance.timestamp;
        this.type = dumpInstance.type;
        this.name = dumpInstance.getName();

    }

    @Override
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
        if (!(o instanceof LightweightDumpInstance)) return false;

        LightweightDumpInstance that = (LightweightDumpInstance) o;

        return name != null ? name.equals(that.name) : that.name == null;

    }

    @Override
    public int hashCode() {
        int result = (name != null ? name.hashCode() : 0);
        return result;
    }
}
