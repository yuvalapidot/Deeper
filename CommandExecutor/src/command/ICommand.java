package command;

import flag.IFlag;

public interface ICommand {

    String getCommand();

    void appendFlag(IFlag flag);
}
