package model.feature;

import model.memory.CallGram;

public class CallGramFeatureKey extends FeatureKey<CallGram, Integer> {

    public CallGramFeatureKey(CallGram feature) {
        super(feature, 0);
    }

    public CallGramFeatureKey(CallGram feature, Integer defaultValue) {
        super(feature, defaultValue);
    }
}
