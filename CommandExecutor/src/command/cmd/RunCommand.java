package command.cmd;

import command.ICommand;
import flag.IFlag;

import java.util.ArrayList;
import java.util.List;

public class RunCommand implements ICommand {

    private final String path;
    private List<IFlag> flags;

    public RunCommand(String path) {
        this.path = path;
        flags = new ArrayList<>();
    }

    @Override
    public String getCommand() {
        StringBuilder builder = new StringBuilder(path);
        for (IFlag flag : flags) {
            builder.append(" ");
            builder.append(flag.getFlagString());
        }
        return builder.toString();
    }

    @Override
    public void appendFlag(IFlag flag) {
        flags.add(flag);
    }
}
