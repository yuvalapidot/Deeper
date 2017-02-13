package creator;

import model.instance.DumpInstance;
import model.instance.InstanceSetType;
import model.memory.Dump;

public class DumpInstanceCreator {

    private int totalCount;
    private int counter;
    private String identification;
    private String classification;
    private double trainSetPercentage;

    public DumpInstanceCreator(String identification, String classification, int totalCount, double trainSetPercentage) {
        this.identification = identification;
        this.classification = classification;
        this.totalCount = totalCount;
        this.trainSetPercentage = trainSetPercentage;
    }

    public DumpInstance create(Dump dump) {
        if (dump.getName().contains(identification) & counter < totalCount) {
            counter++;
            DumpInstance instance = DumpInstance.instance(dump, (counter <= totalCount * trainSetPercentage / 100) ? InstanceSetType.TRAIN_SET : InstanceSetType.TEST_SET, classification);
            instance.setType(identification);
            return instance;
        }
        return null;
    }

    public void initialization() {
        counter = 0;
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public double getTrainSetPercentage() {
        return trainSetPercentage;
    }

    public void setTrainSetPercentage(double trainSetPercentage) {
        this.trainSetPercentage = trainSetPercentage;
    }
}
