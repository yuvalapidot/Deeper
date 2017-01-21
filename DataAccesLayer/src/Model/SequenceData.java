package Model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SequenceData {

    private String sequence;
    private int sequenceLength;

    public SequenceData(String sequence, int sequenceLength) {
        this.sequence = sequence;
        this.sequenceLength = sequenceLength;
    }

    public static List<SequenceData> getSequenceDataList(ResultSet resultSet) throws SQLException {
        List<SequenceData> sequenceDataList = new ArrayList<>();
        while (resultSet.next()) {
            sequenceDataList.add(new SequenceData(resultSet.getString("sequence"),
                    resultSet.getInt("sequence_length")));
        }
        return sequenceDataList;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public int getSequenceLength() {
        return sequenceLength;
    }

    public void setSequenceLength(int sequenceLength) {
        this.sequenceLength = sequenceLength;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SequenceData that = (SequenceData) o;

        if (sequenceLength != that.sequenceLength) return false;
        return sequence != null ? sequence.equals(that.sequence) : that.sequence == null;

    }

    @Override
    public int hashCode() {
        int result = sequence != null ? sequence.hashCode() : 0;
        result = 31 * result + sequenceLength;
        return result;
    }

    @Override
    public String toString() {
        return "SequenceData{" +
                "sequence='" + sequence + '\'' +
                ", sequenceLength=" + sequenceLength +
                '}';
    }
}
