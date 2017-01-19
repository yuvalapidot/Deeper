package dal.sql.sqlite;

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

    public boolean addIntegerColumns(String tableName, List<String> columns);
}
