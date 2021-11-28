package com.oracle.javafx.scenebuilder.api.sample;

import java.util.List;

public interface SampleDataProvider {

    /**
     * Indicate on which object class sample data can be applied/removed
     * @return the applicable class
     */
    List<Class<?>> getApplicableClass();
    /**
     * Add the sample data to the applicable object
     * @param sceneGraphObject
     */
    void applyTo(Object sceneGraphObject);
    /**
     * Remove the sample data from the applicable object
     * @param sceneGraphObject
     */
    void removeFrom(Object sceneGraphObject);


    /**
     * Check done on the scenegraph object prior inserting sample data
     * Typically if (user) data exists in the scenegraph object then no need to add sample data
     * @param sceneGraphObject
     * @return
     */
    boolean canApply(Object sceneGraphObject);
}
