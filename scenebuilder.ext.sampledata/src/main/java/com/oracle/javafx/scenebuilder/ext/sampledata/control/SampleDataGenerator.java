/*
 * Copyright (c) 2016, 2021, Gluon and/or its affiliates.
 * Copyright (c) 2012, 2014, Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation and Gluon nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.oracle.javafx.scenebuilder.ext.sampledata.control;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.sample.SampleDataHandler;
import com.oracle.javafx.scenebuilder.api.sample.SampleDataProvider;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMCollection;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMProperty;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMPropertyC;

/**
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
public class SampleDataGenerator implements SampleDataHandler {

    private final Map<Class<?>, SampleDataProvider> providersMap = new HashMap<>();
    private final Map<FXOMObject, SampleDataProvider> sampleDataMap = new HashMap<>();

    protected SampleDataGenerator(@Autowired(required = false) List<SampleDataProvider> dataProviders) {
        if (dataProviders != null) {
            dataProviders.forEach(p -> p.getApplicableClass().forEach(c -> providersMap.put(c, p)));
        }
    }
    @Override
    public void assignSampleData(FXOMObject startObject) {
        assert startObject != null;

        final Object sceneGraphObject = startObject.getSceneGraphObject();
        final SampleDataProvider currentData = sampleDataMap.get(startObject);
        final SampleDataProvider newData;

        if (sceneGraphObject == null) {
            // startObject is unresolved
            newData = null;
        } else {
            final Class<?> sceneGraphClass = sceneGraphObject.getClass();
            final SampleDataProvider dataProvider = providersMap.get(sceneGraphClass);

            if (dataProvider != null) {
                if (dataProvider.canApply(sceneGraphObject)) {
                    newData = dataProvider;
                } else {
                    newData = null;
                }
            } else {
                newData = null;
            }
        }

        if (newData == null) {
            if (currentData != null) {
                sampleDataMap.remove(startObject);
            }
        } else {
            newData.applyTo(sceneGraphObject);
            sampleDataMap.put(startObject, newData);
        }

        if (startObject instanceof FXOMInstance) {
            final FXOMInstance fxomInstance = (FXOMInstance) startObject;
            for (FXOMProperty p : fxomInstance.getProperties().values()) {
                if (p instanceof FXOMPropertyC) {
                    final FXOMPropertyC pc = (FXOMPropertyC) p;
                    for (FXOMObject v : pc.getValues()) {
                        assignSampleData(v);
                    }
                }
            }
        } else if (startObject instanceof FXOMCollection) {
            final FXOMCollection fxomCollection = (FXOMCollection) startObject;
            for (FXOMObject i : fxomCollection.getItems()) {
                assignSampleData(i);
            }
        }
    }


    @Override
    public void removeSampleData(FXOMObject startObject) {
        final SampleDataProvider currentData = sampleDataMap.get(startObject);
        if (currentData != null) {
            currentData.removeFrom(startObject.getSceneGraphObject());
        }

        if (startObject instanceof FXOMInstance) {
            final FXOMInstance fxomInstance = (FXOMInstance) startObject;
            for (FXOMProperty p : fxomInstance.getProperties().values()) {
                if (p instanceof FXOMPropertyC) {
                    final FXOMPropertyC pc = (FXOMPropertyC) p;
                    for (FXOMObject v : pc.getValues()) {
                        removeSampleData(v);
                    }
                }
            }
        } else if (startObject instanceof FXOMCollection) {
            final FXOMCollection fxomCollection = (FXOMCollection) startObject;
            for (FXOMObject i : fxomCollection.getItems()) {
                removeSampleData(i);
            }
        }
    }

    /*
     * Private
     */
//
//    private AbstractSampleData<?> makeSampleData(FXOMObject fxomObject) {
//        final Object obj = fxomObject.getSceneGraphObject();
//        assert obj == null;
//
//        if (obj instanceof ListView) {
//            @SuppressWarnings("unchecked")
//            final ListView<Object> xyChart = (ListView)obj;
//            return visitList(xyChart);
//        } else if (obj instanceof TreeView) {
//            @SuppressWarnings("unchecked")
//            final TreeView<Object> xyChart = (TreeView)obj;
//            return visitTree(xyChart);
//        } else if (obj instanceof TableView) {
//            @SuppressWarnings("unchecked")
//            final TableView<Object> tableView = (TableView)obj;
//            return visitTable(tableView);
//        } else if (obj instanceof TableColumn) {
//            @SuppressWarnings("unchecked")
//            final TableColumn<Object,Object> tableColumn =
//                (TableColumn<Object,Object>)obj;
//            return visitTableColumn(tableColumn);
//        } else if (obj instanceof XYChart && XYChartSeries.isKnownXYChart(obj)) {
//            @SuppressWarnings("unchecked")
//            final XYChart<Object,Object> chart = (XYChart<Object,Object>)obj;
//            return visitXYChart(chart);
//        } else if (obj instanceof PieChart) {
//            final PieChart chart = (PieChart)obj;
//            return visitPieChart(chart);
//        } else {
//            return Visit.DESCEND;
//        }
//    }
}
