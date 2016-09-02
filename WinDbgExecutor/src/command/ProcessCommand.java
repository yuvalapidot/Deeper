package command;

import flag.HexaFlag;

public class ProcessCommand extends WinDbgCommand {

    private final String command = "!process";
    private final String processId;

    public ProcessCommand(String processId) {
        this.processId = processId;
    }

    public ProcessCommand(String processId, int flags) {
        this.processId = processId;
        appendFlag(new HexaFlag(flags));
    }

    @Override
    protected String getCommandString() {
        return command + " " + processId;
    }
}
