package command;

public class BasicCommand extends WinDbgCommand {

    public BasicCommand(String command) {
        super();
        this.command = command;
    }

    @Override
    protected String getCommandString() {
        return command;
    }
}
