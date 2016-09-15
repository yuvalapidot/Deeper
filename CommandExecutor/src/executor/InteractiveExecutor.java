package executor;

import command.ICommand;
import extractor.IExtractor;

import java.io.*;

public class InteractiveExecutor extends AbstractExecutor {

    private static final char ENTER = '\n';

    private BufferedReader reader;
    private BufferedWriter writer;
    private final Object lock = new Object();

    @Override
    public void execute(ICommand command) throws IOException {
        if (process == null) {
            synchronized (lock) {
                if (process == null) {
                    super.execute(command);

                    reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
                    return;
                }
            }
        }
        log.info("Executing interactive command - " + command.getCommand());
        writer.write(command.getCommand() + ENTER);
        try {
            writer.flush();
        } catch (IOException ex) {
            log.error(ex);
        }

    }

    @Override
    public <T> T getOutput(IExtractor<T> extractor) throws IOException {
        log.info("Trying to get output");
        return extractor.extract(reader);
    }
}
