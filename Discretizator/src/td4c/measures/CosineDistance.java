package td4c.measures;

public class CosineDistance extends AbstractDistanceMeasure {


    @Override
    public double cutoffScore(int numberOfClasses, int[][] bins) {
        double cosineDistance = 0;
        for (int i = 0; i < numberOfClasses; i++) {
            for (int j = i + 1; j < numberOfClasses; j++) {
                cosineDistance += cosine(probability(i, bins), probability(j, bins));
            }
        }
        return cosineDistance;
    }

    @Override
    public String getName() {
        return "Cosine";
    }

    private double cosine(double[] u, double[] v) {
        return vectorMultiplication(u, v) / (vectorialSize(u) * vectorialSize(v));
    }

    private double vectorMultiplication(double[] u, double[] v) {
        double sum = 0;
        for (int i = 0; i < u.length; i++) {
            sum += u[i] * v[i];
        }
        return sum;
    }

    private double vectorialSize(double[] v) {
        double sumOfSquares = 0;
        for (double d : v) {
            sumOfSquares += d * d;
        }
        return Math.sqrt(sumOfSquares);
    }
}
