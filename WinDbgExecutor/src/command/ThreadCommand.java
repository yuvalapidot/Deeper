package command;

import flag.HexaFlag;

public class ThreadCommand extends WinDbgCommand {

    private final String command = "!thread";
    private final String threadId;

    public ThreadCommand(String threadId) {
        this.threadId = threadId;
    }

    public ThreadCommand(String threadId, int flags) {
        this.threadId = threadId;
        appendFlag(new HexaFlag(flags));
    }

    @Override
    protected String getCommandString() {
        return command + " " + threadId;
    }
}
