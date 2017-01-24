package Model;

import model.instance.Instance;
import model.instance.InstanceSetType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DumpInstanceData extends Instance<String> {

    private String dumpName;
    private String dumpType;
    private int dumpTimestamp;
    private String dumpClass;
    private int processCount;
    private int threadCount;

    public DumpInstanceData(String dumpName, String dumpType, int dumpTimestamp, String dumpClass, int processCount, int threadCount) {
        super(dumpName, InstanceSetType.TRAIN_SET, dumpClass);
        this.dumpName = dumpName;
        this.dumpType = dumpType;
        this.dumpTimestamp = dumpTimestamp;
        this.dumpClass = dumpClass;
        this.processCount = processCount;
        this.threadCount = threadCount;
    }

    public static List<DumpInstanceData> getDumpInstanceDataList(ResultSet resultSet) throws SQLException {
        List<DumpInstanceData> dumpInstanceDataList = new ArrayList<>();
        while (resultSet.next()) {
            dumpInstanceDataList.add(new DumpInstanceData(resultSet.getString("dump_name"),
                    resultSet.getString("dump_type"),
                    resultSet.getInt("dump_timestamp"),
                    resultSet.getString("dump_class"),
                    resultSet.getInt("process_count"),
                    resultSet.getInt("thread_count")));
        }
        return dumpInstanceDataList;
    }

    public String getDumpName() {
        return dumpName;
    }

    public void setDumpName(String dumpName) {
        this.dumpName = dumpName;
    }

    public String getDumpType() {
        return dumpType;
    }

    public void setDumpType(String dumpType) {
        this.dumpType = dumpType;
    }

    public int getDumpTimestamp() {
        return dumpTimestamp;
    }

    public void setDumpTimestamp(int dumpTimestamp) {
        this.dumpTimestamp = dumpTimestamp;
    }

    public String getDumpClass() {
        return dumpClass;
    }

    public void setDumpClass(String dumpClass) {
        this.dumpClass = dumpClass;
    }

    public int getProcessCount() {
        return processCount;
    }

    public void setProcessCount(int processCount) {
        this.processCount = processCount;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    @Override
    public String getName() {
        return dumpName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DumpInstanceData that = (DumpInstanceData) o;

        if (dumpTimestamp != that.dumpTimestamp) return false;
        if (processCount != that.processCount) return false;
        if (threadCount != that.threadCount) return false;
        if (dumpName != null ? !dumpName.equals(that.dumpName) : that.dumpName != null) return false;
        if (dumpType != null ? !dumpType.equals(that.dumpType) : that.dumpType != null) return false;
        return dumpClass != null ? dumpClass.equals(that.dumpClass) : that.dumpClass == null;

    }

    @Override
    public int hashCode() {
        int result = dumpName != null ? dumpName.hashCode() : 0;
        result = 31 * result + (dumpType != null ? dumpType.hashCode() : 0);
        result = 31 * result + dumpTimestamp;
        result = 31 * result + (dumpClass != null ? dumpClass.hashCode() : 0);
        result = 31 * result + processCount;
        result = 31 * result + threadCount;
        return result;
    }

    @Override
    public String toString() {
        return "DumpInstanceData{" +
                "dumpName='" + dumpName + '\'' +
                ", dumpType='" + dumpType + '\'' +
                ", dumpTimestamp=" + dumpTimestamp +
                ", dumpClass='" + dumpClass + '\'' +
                ", processCount=" + processCount +
                ", threadCount=" + threadCount +
                '}';
    }
}
