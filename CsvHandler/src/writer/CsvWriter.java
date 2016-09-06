package writer;

import memory.model.CallGramsTable;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CsvWriter {

    private static final char delimiter = ',';
    private static final char nonDelimiter = '|';

    public void writeCallGramsTableToCsv(CallGramsTable table, String outputFilePath) throws IOException {
        String csvString = getCsvString(table);
        FileWriter writer = new FileWriter(outputFilePath);
        writer.write(csvString);
        writer.flush();
        writer.close();
    }

    private String getCsvString(CallGramsTable table) {
        StringBuilder builder = new StringBuilder("Dump" + delimiter);
        int i = 0;
        for (List<?> row : table.toGeneric()) {
            if (i != 0) {
                builder.append(table.getDumps().get(i - 1).getName());
                builder.append(delimiter);
            }
            for (Object object : row) {
                builder.append(object.toString().replace(delimiter, nonDelimiter));
                builder.append(delimiter);
            }
            if (i == 0) {
                builder.append("Class");
            } else {
                builder.append(table.getDumps().get(i - 1).getClassification().toString());
            }
            builder.append('\n');
            i++;
        }
        return builder.toString();
    }
}
