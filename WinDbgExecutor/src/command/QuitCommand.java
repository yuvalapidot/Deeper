package command;

public class QuitCommand extends WinDbgCommand{

    public QuitCommand() {
        command = "q";
    }

    @Override
    protected String getCommandString() {
        return command;
    }
}
