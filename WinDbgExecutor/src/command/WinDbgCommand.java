package command;

import flag.IFlag;

import java.util.ArrayList;
import java.util.List;

public abstract class WinDbgCommand implements ICommand {

    String command;

    private List<IFlag> flags;

    WinDbgCommand() {
        flags = new ArrayList<>();
    }

    protected abstract String getCommandString();

    public final String getCommand() {
        StringBuilder builder = new StringBuilder(getCommandString());
        for (IFlag flag : flags) {
            builder.append(" ");
            builder.append(flag.getFlagString());
        }
        return builder.toString();
    }

    public final void appendFlag(IFlag flag) {
        flags.add(flag);
    }
}
