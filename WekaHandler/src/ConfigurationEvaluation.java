import java.util.Comparator;

public class ConfigurationEvaluation implements Comparable<ConfigurationEvaluation> {

    private WekaEvaluator.ClassifierType classifierType;
    private double threshold;
    private String configuration;
    private double TP;
    private double FP;
    private double AUC;
    private double FMeasure;
    private double score;

    @Override
    public int compareTo(ConfigurationEvaluation o) {
        double value = score - o.score;
        double thresholdVale =  (Math.abs(0.5 - o.threshold)) - (Math.abs(0.5 - threshold));
        return  value < 0 ? -1 : value > 0 ? 1 :
                (thresholdVale < 0 ? -1 : thresholdVale > 0 ? 1 : 0);
    }

    public static Comparator<ConfigurationEvaluation> comparator() {
        return (o1, o2) -> {
            double value = o1.score - o2.score;
            double thresholdVale =  (Math.abs(0.5 - o2.threshold)) - (Math.abs(0.5 - o1.threshold));
            return  value < 0 ? -1 : value > 0 ? 1 :
                    (thresholdVale < 0 ? -1 : thresholdVale > 0 ? 1 : 0);
        };
    }

    public ConfigurationEvaluation(WekaEvaluator.ClassifierType classifierType, double threshold, String configuration, double TP, double FP, double AUC, double FMeasure, double score) {
        this.classifierType = classifierType;
        this.threshold = threshold;
        this.configuration = configuration;
        this.TP = TP;
        this.FP = FP;
        this.AUC = AUC;
        this.FMeasure = FMeasure;
        this.score = score;
    }

    public WekaEvaluator.ClassifierType getClassifierType() {
        return classifierType;
    }

    public void setClassifierType(WekaEvaluator.ClassifierType classifierType) {
        this.classifierType = classifierType;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    public double getTP() {
        return TP;
    }

    public void setTP(double TP) {
        this.TP = TP;
    }

    public double getFP() {
        return FP;
    }

    public void setFP(double FP) {
        this.FP = FP;
    }

    public double getAUC() {
        return AUC;
    }

    public void setAUC(double AUC) {
        this.AUC = AUC;
    }

    public double getFMeasure() {
        return FMeasure;
    }

    public void setFMeasure(double FMeasure) {
        this.FMeasure = FMeasure;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "ConfigurationEvaluation{" +
                "classifierType=" + classifierType +
                ", threshold=" + threshold +
                ", configuration=" + configuration +
                ", TP=" + TP +
                ", FP=" + FP +
                ", AUC=" + AUC +
                ", FMeasure=" + FMeasure +
                ", score=" + score +
                '}';
    }
}
