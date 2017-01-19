package model.memory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.lang.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Dump {

    private List<Process> processes;
    private String classification = "Unknown";
    private String name;

    public Dump() {
    }

    public Dump(String name, String classification, List<Process> processes) {
        this.name = name;
        this.classification = classification;
        this.processes = processes;
    }

    public Dump(String name) {
        this.name = name;
        processes = new ArrayList<>();
    }

    public void setProcesses(List<Process> processes) {
        this.processes = processes;
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

    public void setName(String name) {
        this.name = name;
    }

    public int processCount() {
        return processes.size();
    }

    public int threadCount() {
        int count = 0;
        for (Process process : processes) {
            count += process.getThreads().size();
        }
        return count;
    }
}
