package model.instance;

import model.feature.Instance;
import model.memory.Dump;

public class DumpInstance extends Instance {

    public DumpInstance(Dump dump) {
        super(dump.getName());
    }
}
