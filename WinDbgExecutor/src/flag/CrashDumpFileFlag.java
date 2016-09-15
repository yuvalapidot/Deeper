package flag;

public class CrashDumpFileFlag implements IFlag {

    private static final String FLAG = "-z ";
    private final String path;

    public CrashDumpFileFlag(String path) {
        this.path = path;
    }

    @Override
    public String getFlagString() {
        return FLAG + path;
    }
}
