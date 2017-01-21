import Model.DumpInstanceData;
import Model.DumpSequenceRelationData;
import Model.SequenceData;
import dal.sql.sqlite.DataAccessLayer;
import dal.sql.sqlite.IDataAccessLayer;
import java.io.IOException;
import java.sql.SQLException;

public class MainSequencesFromDB {

    public static void main(String[] args) throws IOException, SQLException {
        try (IDataAccessLayer dal = new DataAccessLayer()) {
            for (DumpInstanceData data : dal.selectDumps()) {
                System.out.println(data.toString());
            }
            for (SequenceData data : dal.selectSequences()) {
                System.out.println(data.toString());
            }
            for (DumpSequenceRelationData data : dal.selectDumpSequenceRelation()) {
                System.out.println(data.toString());
            }
        }
    }

}
