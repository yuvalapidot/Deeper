import Model.DumpInstanceData;
import Model.DumpSequenceRelationData;
import Model.SequenceData;
import dal.sql.sqlite.DataAccessLayer;
import dal.sql.sqlite.IDataAccessLayer;
import model.data.DataTable;
import model.instance.InstanceSetType;
import td4c.TD4CDiscretizator;
import td4c.measures.KullbackLeiblerDistance;
import writer.CsvNumberRepresentation;
import writer.DataTableCsvWriter;
import writer.DataTableToCsvRequest;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MainSequencesFromDB {

    private static final String csvPath = "D:\\Dropbox\\NGrams\\Results\\Sequences\\Experiment 1\\kl_1.csv";
    private static final Set<InstanceSetType> TRAIN_TEST = new HashSet<>(Arrays.asList(InstanceSetType.TRAIN_SET, InstanceSetType.TEST_SET));

    public static void main(String[] args) throws IOException, SQLException {
        DataTable table;
        try (IDataAccessLayer dal = new DataAccessLayer()) {
            table = dal.getDataTable();

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
        TD4CDiscretizator discretizator = new TD4CDiscretizator(table.getInstances(), new KullbackLeiblerDistance());
        discretizator.discrete(table, 3);
        DataTableCsvWriter writer = new DataTableCsvWriter();
        writer.dataTableToCsv(new DataTableToCsvRequest(table, csvPath, CsvNumberRepresentation.INTEGER_REPRESENTATION, TRAIN_TEST, 1, 0.1));
    }

}
