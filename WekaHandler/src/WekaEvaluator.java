import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.meta.ThresholdSelector;
import weka.classifiers.trees.J48;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class WekaEvaluator {

    private static final Logger log = LogManager.getLogger(WekaEvaluator.class);

    private static final int numberOfConfigurations = 10;

    private double[] thresholds = new double[] {0.4, 0.45, 0.5, 0.55, 0.6, 0.65, 0.7};

    private List<ClassifierType> classifiers;
    private List<List<ConfigurationEvaluation>> configurationEvaluations;
//    private String[][] configurations;
//    private double[][] scores;

    public WekaEvaluator() {
        classifiers = new ArrayList<>();
        classifiers.add(ClassifierType.J48);
        classifiers.add(ClassifierType.RandomForest);
        classifiers.add(ClassifierType.NaiveBase);
        classifiers.add(ClassifierType.BayesNetwork);
        classifiers.add(ClassifierType.LibSvm);
        configurationEvaluations = new ArrayList<>();
        for (ClassifierType classifierType : classifiers) {
            configurationEvaluations.add(new ArrayList<>());
        }
//        configurations = new String[classifiers.size()][numberOfConfigurations];
//        scores = new double[classifiers.size()][numberOfConfigurations];
    }

    private Classifier getClassifier(ClassifierType classifierType, double threshold) throws Exception {
        switch (classifierType) {
            case J48: return getThresholdSelector(threshold, new J48());
            case RandomForest: return getThresholdSelector(threshold, new RandomForest());
            case NaiveBase: return getThresholdSelector(threshold, new NaiveBayes());
            case BayesNetwork: return getThresholdSelector(threshold, new BayesNet());
            case LibSvm: return getThresholdSelector(threshold, new LibSVM());
            default: return getThresholdSelector(threshold, new J48());
        }
    }

    private Classifier getThresholdSelector(double threshold, Classifier classifier) throws Exception {
        ThresholdSelector thresholdSelector = new ThresholdSelector();
        thresholdSelector.setManualThresholdValue(threshold);
        thresholdSelector.setClassifier(classifier);
        return thresholdSelector;
    }

    private Evaluation evaluate(String trainPath, String testPath, Classifier classifier) throws Exception {
        DataSource trainSource = new DataSource(trainPath);
        Instances train = trainSource.getDataSet();
        train.setClassIndex(train.numAttributes() - 1);
        DataSource testSource = new DataSource(testPath);
        Instances test = testSource.getDataSet();
        test.setClassIndex(test.numAttributes() - 1);
        classifier.buildClassifier(train);
        Evaluation evaluation = new Evaluation(train);
        evaluation.evaluateModel(classifier, test);
        return evaluation;
    }

    public void evaluateOnConfiguration(String configuration, List<String[]> paths) throws Exception {
        int batchSize = paths.size();
        for (int i = 0; i < classifiers.size(); i++) {
            for (double threshold : thresholds) {
                double sumTruePositive = 0;
                double sumFalsePositive = 0;
                double sumAuc = 0;
                double sumFMeasure = 0;
                Classifier classifier = getClassifier(classifiers.get(i), threshold);
                for (String[] path : paths) {
                    Evaluation evaluation = evaluate(path[0], path[1], classifier);
                    sumTruePositive += evaluation.truePositiveRate(0);
                    sumFalsePositive += evaluation.falsePositiveRate(0);
                    sumAuc += evaluation.areaUnderROC(0);
                    sumFMeasure += evaluation.fMeasure(0);
                }
                double averageFP = (sumFalsePositive / batchSize);
                double averageTP = (sumTruePositive / batchSize);
                double averageAUC = (sumAuc / batchSize);
                double averageFMEASURE = (sumFMeasure / batchSize);
                double score = averageFP <= 0.05 ? averageTP : (averageFP <= 0.1 ? averageTP - 0.1 : (averageFP <= 0.15 ? averageTP - 0.2 : (averageFP <= 0.2 ? averageTP - 0.3 : 0)));
                ConfigurationEvaluation configurationEvaluation = new ConfigurationEvaluation(classifiers.get(i), threshold, configuration, averageTP, averageFP, averageAUC, averageFMEASURE, score);
                log.info(configurationEvaluation.toString());
                configurationEvaluations.get(i).add(configurationEvaluation);
            }
        }
    }

    public void printBest() {
        log.info("Going to write best " + numberOfConfigurations + " configurations for each classifier");
        for (List<ConfigurationEvaluation> configurationEvaluationsForClassifier : configurationEvaluations) {
            configurationEvaluationsForClassifier.sort(ConfigurationEvaluation.comparator().reversed());
            for (int i = 0; i < Math.min(numberOfConfigurations, configurationEvaluationsForClassifier.size()); i++) {
                log.info(i + ") " + configurationEvaluationsForClassifier.get(i).toString());
            }
        }
    }

    public enum ClassifierType {
        J48,
        RandomForest,
        NaiveBase,
        BayesNetwork,
        LibSvm
    }
}
