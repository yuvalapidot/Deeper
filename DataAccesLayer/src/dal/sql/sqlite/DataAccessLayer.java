package dal.sql.sqlite;

import Model.DumpInstanceData;
import Model.DumpSequenceRelationData;
import Model.SequenceData;
import javafx.util.Pair;
import model.data.DataTable;
import model.instance.DumpInstance;
import model.instance.Instance;
import model.memory.Sequence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import properties.Configuration;

import java.io.IOException;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DataAccessLayer implements IDataAccessLayer {

    private static final Logger log = LogManager.getLogger(DataAccessLayer.class);

    private final String dumpsTableName = "dumps";
    private final String sequenceTableName = "sequences";
    private final String dumpSequenceTableName = "dump_sequence";

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
    public boolean createDumpsTable() {
        String sql = "CREATE TABLE IF NOT EXISTS " + dumpsTableName + " (\n"
                + "	dump_name text PRIMARY KEY,\n"
                + "	dump_type text NULL,\n"
                + "	dump_timestamp integer NULL,\n"
                + "	dump_class text NOT NULL,\n"
                + " process_count integer NOT NULL,"
                + " thread_count integer NOT NULL"
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
    public boolean createSequenceTable() {
        String sql = "CREATE TABLE IF NOT EXISTS " + sequenceTableName + " (\n"
                + "	sequence text PRIMARY KEY,\n"
                + "	sequence_length integer NOT NULL\n"
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
    public boolean createDumpSequenceTable() {
        String sql = "CREATE TABLE IF NOT EXISTS " + dumpSequenceTableName + " (\n"
                + "	dump_name text NOT NULL,\n"
                + "	sequence text NOT NULL,\n"
                + "	sequence_count integer NOT NULL,\n"
                + "PRIMARY KEY (dump_name, sequence),\n"
                + "FOREIGN KEY(dump_name) REFERENCES " + dumpsTableName + "(dump_name),\n"
                + "FOREIGN KEY(sequence) REFERENCES " + sequenceTableName + "(sequence)\n"
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
    public boolean insertDumpsBatch(List<DumpInstance> dumps) {
        String sql = "INSERT INTO " + dumpsTableName
                + "(dump_name,dump_type,dump_timestamp,dump_class,process_count,thread_count) VALUES(?,?,?,?,?,?)";
        log.info("Going to perform query" + sql + " for " + dumps.size() + " dumps.");
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (DumpInstance dump : dumps) {
                statement.setString(1, dump.getName());
                statement.setString(2, dump.getType());
                statement.setInt(3, dump.getTimestamp());
                statement.setString(4, dump.getClassification());
                statement.setInt(5, dump.getInstance().processCount());
                statement.setInt(6, dump.getInstance().threadCount());
                statement.addBatch();
            }
            return statement.executeBatch() != null;
        } catch (SQLException ex) {
            log.error("Encountered an error during table insertion", ex);
            return false;
        }
    }

    @Override
    public boolean insertSequence(Sequence sequence) {
        String sql = "INSERT INTO " + sequenceTableName
                + "(sequence,sequence_length) VALUES(?,?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, sequence.toString());
            statement.setInt(2, sequence.size());
            return (statement.executeUpdate() != 0);
        } catch (SQLException ex) {
            log.error("Encountered an error during table insertion", ex);
            return false;
        }
    }

    @Override
    public boolean insertSequencesBatch(List<Sequence> sequences) {
        String sql = "INSERT INTO " + sequenceTableName
                + "(sequence,sequence_length) VALUES(?,?)";
        log.info("Going to perform query" + sql + " for " + sequences.size() + " sequences.");
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (Sequence sequence : sequences) {
                statement.setString(1, sequence.toString());
                statement.setInt(2, sequence.size());
                statement.addBatch();
            }
            return statement.executeBatch() != null;
        } catch (SQLException ex) {
            log.error("Encountered an error during table insertion", ex);
            return false;
        }
    }

    @Override
    public boolean insertDumpSequenceRelation(DumpInstance dump, Sequence sequence, int count) {
        String sql = "INSERT INTO " + dumpSequenceTableName
                + "(dump_name,sequence,sequence_count) VALUES(?,?,?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, dump.getName());
            statement.setString(2, sequence.toString());
            statement.setInt(3, count);
            return (statement.executeUpdate() != 0);
        } catch (SQLException ex) {
            log.error("Encountered an error during table insertion", ex);
            return false;
        }
    }

    @Override
    public boolean insertDumpSequenceRelationBatch(Map<Sequence, List<Pair<DumpInstance, Integer>>> map) {
        boolean success = true;
        String sql = "INSERT INTO " + dumpSequenceTableName
                + "(dump_name,sequence,sequence_count) VALUES(?,?,?)";
        for (Sequence sequence : map.keySet()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                for (Pair<DumpInstance, Integer> dumpInfo : map.get(sequence)) {
                    statement.setString(1, dumpInfo.getKey().getName());
                    statement.setString(2, sequence.toString());
                    statement.setInt(3, dumpInfo.getValue());
                    statement.addBatch();
                }
                success &= statement.executeBatch() != null;
            } catch (SQLException ex) {
                log.error("Encountered an error during table insertion", ex);
                success = false;
            }
        }
        return success;
    }

    @Override
    public List<DumpInstanceData> selectDumps() {
        String sql = "SELECT dump_name, dump_type, dump_timestamp, dump_class, process_count, thread_count FROM " + dumpsTableName;
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(sql);
            return DumpInstanceData.getDumpInstanceDataList(resultSet);
        } catch (SQLException ex) {
            log.error("Encountered an error during table selection", ex);
            return null;
        }
    }

    @Override
    public List<SequenceData> selectSequences() {
        String sql = "SELECT sequence, sequence_length FROM " + sequenceTableName;
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(sql);
            return SequenceData.getSequenceDataList(resultSet);
        } catch (SQLException ex) {
            log.error("Encountered an error during table selection", ex);
            return null;
        }
    }

    @Override
    public List<DumpSequenceRelationData> selectDumpSequenceRelation() {
        String sql = "SELECT dump_name, sequence, sequence_count FROM " + dumpSequenceTableName;
        return getDumpSequenceRelationData(sql);
    }

    @Override
    public List<DumpSequenceRelationData> selectDumpSequenceRelationByDump(String dumpName) {
        String sql = "SELECT dump_name, sequence, sequence_count FROM " + dumpSequenceTableName
                + "\nWHERE dump_name = " + dumpName;
        return getDumpSequenceRelationData(sql);
    }

    @Override
    public List<DumpSequenceRelationData> selectDumpSequenceRelationBySequence(String sequence) {
        String sql = "SELECT dump_name, sequence, sequence_count FROM " + dumpSequenceTableName
                + "\nWHERE sequence = " + sequence;
        return getDumpSequenceRelationData(sql);
    }

    @Override
    public List<DumpSequenceRelationData> selectDumpSequenceRelation(String dumpName, String sequence) {
        String sql = "SELECT dump_name, sequence, sequence_count FROM " + dumpSequenceTableName
                + "\nWHERE dump_name = " + dumpName + " AND sequence = " + sequence;
        return getDumpSequenceRelationData(sql);
    }

    public DataTable getDataTable() {
        Map<String, Instance> instanceMap = getInstanceMap();
        DataTable table = new DataTable();
        for (SequenceData sequenceData : selectSequences()) {
            String key = sequenceData.getSequence();
            for (DumpSequenceRelationData dumpSequenceRelationData : selectDumpSequenceRelationBySequence(sequenceData.getSequence())) {
                Integer value = dumpSequenceRelationData.getCount();
                table.put(instanceMap.get(dumpSequenceRelationData.getDumpName()), key, value);
            }
        }
        return table;
    }

    private Map<String, Instance> getInstanceMap() {
        List<DumpInstanceData> dumpInstanceDataList = selectDumps();
        Map<String, Instance> dumpInstanceMap = new LinkedHashMap<>();
        for (DumpInstanceData data : dumpInstanceDataList) {
            dumpInstanceMap.put(data.getDumpName(), data);
        }
        return dumpInstanceMap;
    }

    private List<DumpSequenceRelationData> getDumpSequenceRelationData(String sql) {
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(sql);
            return DumpSequenceRelationData.getDumpSequenceRelationDataList(resultSet);
        } catch (SQLException ex) {
            log.error("Encountered an error during table selection", ex);
            return null;
        }
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
