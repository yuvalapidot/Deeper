package td4c.measures;

import model.data.DataTable;

public interface IDistanceMeasure {

    public double cutoffScore(int numberOfClasses, int[][] bins);

    String getName();
}
