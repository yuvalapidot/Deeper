package command;

public class ForEachThreadCommand extends WinDbgCommand {

    private final ICommand innerCommand;

    public ForEachThreadCommand(ICommand innerCommand) {
        this.innerCommand = innerCommand;
        command = "!for_each_thread";
    }

    public ForEachThreadCommand(){
        this(new EmptyCommand());
    }

    @Override
    protected String getCommandString() {
        return command + " \"" + innerCommand.getCommand() + "\"";
    }
}
