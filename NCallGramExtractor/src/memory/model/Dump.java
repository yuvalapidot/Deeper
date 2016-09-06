package memory.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Dump {

    private List<Process> processes;
    private DumpClassification classification = DumpClassification.Unknown;
    private final String name;

    public Dump(String name) {
        this.name = name;
        processes = new ArrayList<>();
    }

    public void setProcesses(List<Process> processes) {
        this.processes = processes;
    }

    public DumpClassification getClassification() {
        return classification;
    }

    public void setClassification(DumpClassification classification) {
        this.classification = classification;
    }

    public List<Process> getProcesses() {
        return processes;
    }

    public void addProcess(Process process) {
        processes.add(process);
    }

    public void addProcesses(Collection<Process> processes) {
        this.processes.addAll(processes);
    }

    public String getName() {
        return name;
    }
}
