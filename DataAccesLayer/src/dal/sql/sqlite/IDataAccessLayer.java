package dal.sql.sqlite;

import java.io.Closeable;
import java.util.List;

public interface IDataAccessLayer extends Closeable {

    public boolean createDumpsTable(String tableName);

    public boolean addIntegerColumns(String tableName, List<String> columns);
}
