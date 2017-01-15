package dal.sql.sqlite;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import properties.Configuration;

import java.io.IOException;
import java.sql.*;
import java.util.List;

public class DataAccessLayer implements IDataAccessLayer {

    private static final Logger log = LogManager.getLogger(DataAccessLayer.class);

    private Connection connection;

    public DataAccessLayer() throws SQLException {
        startConnection();
    }

    private void startConnection() throws SQLException {
        connection = DriverManager.getConnection(Configuration.getString("CONNECTION_STRING"));
    }

    private void endConnection() throws SQLException {
        connection.close();
    }

    @Override
    public boolean createDumpsTable(String tableName) {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (\n"
                + "	dump_name text PRIMARY KEY,\n"
                + "	dump_type text NOT NULL,\n"
                + "	dump_timestamp integer NOT NULL,\n"
                + "	dump_class text NOT NULL,\n"
                + " process_count integer NOT NULL,"
                + " threads_count integer NOT NULL"
                + ");";
        log.info(sql);
        try (Statement statement = connection.createStatement()) {
            return statement.execute(sql);
        } catch (SQLException ex) {
            log.error("Encountered an error during table creation", ex);
            return false;
        }
    }

    @Override
    public boolean addIntegerColumns(String tableName, List<String> columns) {
        boolean success = true;
            for (String columnName : columns) {
                String sql = "ALTER TABLE " + tableName + "\n"
                        + "ADD COLUMN " + columnName + " integer default 0;";
                log.info(sql);
                try (Statement statement = connection.createStatement()) {
                    success &= statement.execute(sql);
                } catch (SQLException ex) {
                    log.error("Encountered an error during table altering", ex);
                }
            }
        return success;
    }


    @Override
    public void close() throws IOException {
        try {
            endConnection();
        } catch (SQLException ex) {
            throw new IOException("Exception has occurred during connection jdbc closing.", ex);
        }
    }
}
