package model.feature;

public class BinaryFeatureValue extends FeatureValue<Integer> {

    public static BinaryFeatureValue TRUE_VALUE = new BinaryFeatureValue(1);
    public static BinaryFeatureValue FALSE_VALUE = new BinaryFeatureValue(0);

    private BinaryFeatureValue(Integer value) {
        super(value);
    }
}
