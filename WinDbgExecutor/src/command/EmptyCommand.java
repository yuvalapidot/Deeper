package command;

public class EmptyCommand extends WinDbgCommand {

    public EmptyCommand() {
        command = "";
    }

    @Override
    protected String getCommandString() {
        return command;
    }
}
