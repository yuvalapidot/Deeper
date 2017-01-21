package Model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DumpSequenceRelationData {

    private String dumpName;
    private String sequence;
    private int count;

    public DumpSequenceRelationData(String dumpName, String sequence, int count) {
        this.dumpName = dumpName;
        this.sequence = sequence;
        this.count = count;
    }

    public static List<DumpSequenceRelationData> getDumpSequenceRelationDataList(ResultSet resultSet) throws SQLException {
        List<DumpSequenceRelationData> dumpSequenceRelationDataList = new ArrayList<>();
        while (resultSet.next()) {
            dumpSequenceRelationDataList.add(new DumpSequenceRelationData(resultSet.getString("dump_name"),
                    resultSet.getString("sequence"),
                    resultSet.getInt("sequence_count")));
        }
        return dumpSequenceRelationDataList;
    }

    public String getDumpName() {
        return dumpName;
    }

    public void setDumpName(String dumpName) {
        this.dumpName = dumpName;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DumpSequenceRelationData that = (DumpSequenceRelationData) o;

        if (count != that.count) return false;
        if (dumpName != null ? !dumpName.equals(that.dumpName) : that.dumpName != null) return false;
        return sequence != null ? sequence.equals(that.sequence) : that.sequence == null;

    }

    @Override
    public int hashCode() {
        int result = dumpName != null ? dumpName.hashCode() : 0;
        result = 31 * result + (sequence != null ? sequence.hashCode() : 0);
        result = 31 * result + count;
        return result;
    }

    @Override
    public String toString() {
        return "DumpSequenceRelationData{" +
                "dumpName='" + dumpName + '\'' +
                ", sequence='" + sequence + '\'' +
                ", count=" + count +
                '}';
    }
}
