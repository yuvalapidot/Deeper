package api;

import command.*;
import executor.AbstractExecutor;
import executor.InteractiveExecutor;
import extractor.CallStackWinDbgExtractor;
import extractor.ProcessesWinDbgExtractor;
import extractor.ThreadWinDbgExtractor;
import extractor.VoidWinDbgExtractor;
import flag.CrashDumpFileFlag;
import model.memory.Dump;
import model.memory.Process;
import model.memory.Thread;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;

public class WinDbgAPI {

    private static final Logger log = LogManager.getLogger(WinDbgAPI.class);

    private static AbstractExecutor startWinDbg(String dump) throws IOException {
        log.info("Starting WinDbg with dump file - " + dump);
        ICommand command = new RunWinDbgCommand();
        command.appendFlag(new CrashDumpFileFlag(dump));
        InteractiveExecutor executor = new InteractiveExecutor();
        executor.execute(command);
        executor.getOutput(new VoidWinDbgExtractor());
        log.info("WinDbg started and ready for use");
        return executor;
    }

    private static int quitExecutor(AbstractExecutor executor) throws IOException {
        log.info("Quiting WindDbg");
        executor.execute(new QuitCommand());
        int exitStatus = executor.waitFor();
        log.info("WinDbg existed with status - " + exitStatus);
        return exitStatus;
    }

    public static Dump getDump(String dumpPath, String classification) throws IOException {
        Dump dump = new Dump(dumpPath);
        List<Process> processes = getProcessesFromDump(dumpPath);
        dump.addProcesses(processes);
        dump.setClassification(classification);
        return dump;
    }

    public static List<Process> getProcessesFromDump(String dump) throws IOException {
        log.info("Will now try to get all processes from dump - " + dump);
        AbstractExecutor executor = startWinDbg(dump);
        List<Process> processes = getProcesses(executor);
        quitExecutor(executor);
        log.info("Found " + processes.size() + " processes");
        return processes;
    }

    public static List<Process> getProcesses(AbstractExecutor executor) throws IOException {
        log.info("Getting all processes information");
        List<Process> processes = getBasicProcesses(executor);
        addThreadsToProcesses(executor, processes);
        return processes;
    }

    public static List<Process> getBasicProcesses(AbstractExecutor executor) throws IOException {
        log.info("Getting all processes");
        executor.execute(new ForEachProcessCommand());
        return executor.getOutput(new ProcessesWinDbgExtractor());
    }

    private static void addThreadsToProcesses(AbstractExecutor executor, List<Process> processes) throws IOException {
        for (Process process : processes) {
            addThreadsToProcess(executor, process);
        }
    }

    public static void addThreadsToProcess(AbstractExecutor executor, Process process) throws IOException {
        log.info("Getting all threads for process " + process.getId());
        executor.execute(new ProcessCommand(process.getId()));
        List<Thread> threads = executor.getOutput(new ThreadWinDbgExtractor());
        addCallStackToThreads(executor, threads);
        process.addThreads(threads);
    }

    public static List<Thread> getThreadsFromDump(String dump) throws IOException {
        log.info("Will now try to get all threads from dump - " + dump);
        AbstractExecutor executor = startWinDbg(dump);
        List<Thread> threads = getThreads(executor);
        quitExecutor(executor);
        log.info("Found " + threads.size() + " threads");
        return threads;
    }

    public static List<Thread> getThreads(AbstractExecutor executor) throws IOException {
        log.info("Getting all threads information");
        List<Thread> threads = getBasicThreads(executor);
        addCallStackToThreads(executor, threads);
        return threads;
    }

    public static List<Thread> getBasicThreads(AbstractExecutor executor) throws IOException {
        log.info("Getting all threads");
        executor.execute(new ForEachThreadCommand());
        return executor.getOutput(new ThreadWinDbgExtractor());
    }

    private static void addCallStackToThreads(AbstractExecutor executor, List<Thread> threads) throws IOException {
        for (Thread thread : threads) {
            addCallStackToThread(executor, thread);
        }
    }

    public static void addCallStackToThread(AbstractExecutor executor, Thread thread) throws IOException {
        int flags = 0x16;
        log.info("Getting call stack for thread " + thread.getId());
        executor.execute(new ThreadCommand(thread.getId(), flags));
        thread.setCallStack(executor.getOutput(new CallStackWinDbgExtractor()));
    }
}
