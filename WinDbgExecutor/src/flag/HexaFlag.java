package flag;

public class HexaFlag implements IFlag {

    private final int number;

    public HexaFlag(int flag_number) {
        this.number = flag_number;
    }

    @Override
    public String getFlagString() {
        return Integer.toHexString(number);
    }
}
