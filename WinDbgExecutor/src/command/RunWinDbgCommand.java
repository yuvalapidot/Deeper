package command;

import command.cmd.RunCommand;
import properties.Configuration;

public class RunWinDbgCommand extends RunCommand {

    public RunWinDbgCommand() {
        super(Configuration.getString("CDB_PATH"));
    }
}
