package com.oracle.javafx.scenebuilder.api.sample;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;

public interface SampleDataHandler {

    void assignSampleData(FXOMObject startObject);

    void removeSampleData(FXOMObject startObject);

}