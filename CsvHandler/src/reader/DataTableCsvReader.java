package reader;

import model.data.DataTable;
import model.instance.Instance;
import model.instance.StringInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class DataTableCsvReader {

    private final Logger log = LogManager.getLogger(DataTableCsvReader.class);

    private static final char CSV_DELIMITER = ',';

    public DataTable csvToDataTable(CsvToDataTableRequest request) {
        log.info("Trying to read Data Table from csv: " + request.getCsvPath());
        DataTable dataTable = new DataTable();
        try (FileReader reader = new FileReader(request.getCsvPath());
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            String line = bufferedReader.readLine();
            String[] featureKeyNames = line.split(String.valueOf(CSV_DELIMITER));
            int instanceCount = 0;
            while ((line = bufferedReader.readLine()) != null) {
                instanceCount++;
                String[] instanceValueStrings = line.split(String.valueOf(CSV_DELIMITER));
                Instance instance = new StringInstance(request.isNamesOnFirstColumn() ? instanceValueStrings[0] : "Instance " + instanceCount);
                for (int i = request.isNamesOnFirstColumn() ? 1 : 0; i < Math.min(featureKeyNames.length, instanceValueStrings.length); i++) {
                    dataTable.put(instance, featureKeyNames[i], instanceValueStrings[i]);
                }
            }
        } catch (FileNotFoundException e) {
            log.info("Csv: " + request.getCsvPath()+ " was not found", e);
            e.printStackTrace();
        } catch (IOException e) {
            log.info("Encountered an error while reading csv: " + request.getCsvPath(), e);
            e.printStackTrace();
        }
        log.info("Finished reading Data Table from csv: " + request.getCsvPath());
        request.notifyObservers(dataTable);
        return dataTable;
    }
}
