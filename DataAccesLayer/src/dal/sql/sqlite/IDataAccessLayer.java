package dal.sql.sqlite;

import Model.DumpInstanceData;
import Model.DumpSequenceRelationData;
import Model.SequenceData;
import javafx.util.Pair;
import model.data.DataTable;
import model.instance.DumpInstance;
import model.memory.Sequence;

import java.io.Closeable;
import java.util.List;
import java.util.Map;

public interface IDataAccessLayer extends Closeable {

    public boolean createDumpsTable();

    public boolean createSequenceTable();

    public boolean createDumpSequenceTable();

    public boolean insertDumpsBatch(List<DumpInstance> dumps);

    public boolean insertSequence(Sequence sequence);

    public boolean insertSequencesBatch(List<Sequence> sequences);

    public boolean insertDumpSequenceRelation(DumpInstance dump, Sequence sequence, int count);

    public boolean insertDumpSequenceRelationBatch(Map<Sequence, List<Pair<DumpInstance, Integer>>> map);

    public List<DumpInstanceData> selectDumps();

    public List<SequenceData> selectSequences();

    public List<DumpSequenceRelationData> selectDumpSequenceRelation();

    public List<DumpSequenceRelationData> selectDumpSequenceRelationByDump(String dumpName);

    public List<DumpSequenceRelationData> selectDumpSequenceRelationBySequence(String sequence);

    public List<DumpSequenceRelationData> selectDumpSequenceRelation(String dumpName, String sequence);

    public DataTable getDataTable();
}
