package command;

public class ForEachProcessCommand extends WinDbgCommand {

    private final ICommand innerCommand;

    public ForEachProcessCommand(ICommand innerCommand) {
        this.innerCommand = innerCommand;
        command = "!for_each_process";
    }

    public ForEachProcessCommand(){
        this(new EmptyCommand());
    }

    @Override
    protected String getCommandString() {
        return command + " \"" + innerCommand.getCommand() + "\"";
    }
}
