package dal.sql.sqlite;

import Model.DumpInstanceData;
import Model.DumpSequenceRelationData;
import Model.SequenceData;
import model.instance.DumpInstance;
import model.memory.Sequence;

import java.io.Closeable;
import java.util.List;

public interface IDataAccessLayer extends Closeable {

    public boolean createDumpsTable();

    public boolean createSequenceTable();

    public boolean createDumpSequenceTable();

    public boolean insertDumps(List<DumpInstance> dumps);

    public boolean insertSequence(Sequence sequence);

    public boolean insertSequences(List<Sequence> sequences);

    public boolean insertDumpSequenceRelation(DumpInstance dump, Sequence sequence, int count);

    public List<DumpInstanceData> selectDumps();

    public List<SequenceData> selectSequences();

    public List<DumpSequenceRelationData> selectDumpSequenceRelation();

    public List<DumpSequenceRelationData> selectDumpSequenceRelationByDump(String dumpName);

    public List<DumpSequenceRelationData> selectDumpSequenceRelationBySequence(String sequence);

    public List<DumpSequenceRelationData> selectDumpSequenceRelation(String dumpName, String sequence);
}
