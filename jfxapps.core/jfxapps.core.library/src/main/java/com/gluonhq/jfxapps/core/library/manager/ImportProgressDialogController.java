/*
 * Copyright (c) 2016, 2024, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2024, Pascal Treilhes and/or its affiliates.
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
package com.gluonhq.jfxapps.core.library.manager;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationEvents;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.IconSetting;
import com.gluonhq.jfxapps.core.api.ui.dialog.AbstractModalDialog;
import com.gluonhq.jfxapps.core.api.ui.dialog.Dialog;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
/**
 *
 */
@ApplicationInstanceSingleton
public class ImportProgressDialogController extends AbstractModalDialog {

    private static final String I18N_IMPORT_ERROR_DETAILS = "import.error.details";

    private static final String I18N_IMPORT_ERROR_MESSAGE = "import.error.message";

    private static final String I18N_IMPORT_ERROR_TITLE = "import.error.title";

    private static final String I18N_IMPORT_WINDOW_TITLE = "import.window.title";

    private static final Logger logger = LoggerFactory.getLogger(ImportProgressDialogController.class);

    private int numOfImportedJar;

    private Stage owner;

    @FXML
    private Label processingLabel;

    @FXML
    ProgressIndicator processingProgressIndicator;

    @FXML
    private VBox tasksHolder;

    private final Dialog dialog;

    private List<Task<?>> currentTasks;

    // @formatter:off
    protected ImportProgressDialogController(
            I18N i18n,
            ApplicationEvents sceneBuilderManager,
            IconSetting iconSetting,
            Dialog dialog
            ) {
     // @formatter:on
        super(i18n, sceneBuilderManager, iconSetting, ImportProgressDialogController.class.getResource("ImportProgressDialog.fxml"), null);
        this.dialog = dialog;
    }


    /*
     * Event handlers
     */
    /* TO BE SOLVED
     We have an issue with the exploration of SOME jar files.
     If e.g. you use sa-jdi.jar (take it in the JRE or JDK tree) then a NPE as
     the one below will be printed but cannot be caught in the code of this class.
     And from there we won't be able to exit from SB, whatever the action we take
     on the import window (Cancel or Import).
     Yes the window goes away but some thread refuse to give up.
     I noticed two non daemon threads:
     AWT-EventQueue-0
     AWT-Shutdown

     java.lang.NullPointerException
     at java.util.StringTokenizer.<init>(StringTokenizer.java:199)
     at java.util.StringTokenizer.<init>(StringTokenizer.java:221)
     at sun.jvm.hotspot.tools.jcore.PackageNameFilter.<init>(PackageNameFilter.java:41)
     at sun.jvm.hotspot.tools.jcore.PackageNameFilter.<init>(PackageNameFilter.java:36)
     at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
     at sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:57)
     at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
     at java.lang.reflect.Constructor.newInstance(Constructor.java:414)
     at java.lang.Class.newInstance(Class.java:444)
     at sun.reflect.misc.ReflectUtil.newInstance(ReflectUtil.java:47)
     at javafx.fxml.FXMLLoader$InstanceDeclarationElement.constructValue(FXMLLoader.java:883)
     at javafx.fxml.FXMLLoader$ValueElement.processStartElement(FXMLLoader.java:614)
     at javafx.fxml.FXMLLoader.processStartElement(FXMLLoader.java:2491)
     at javafx.fxml.FXMLLoader.load(FXMLLoader.java:2300)
     at com.oracle.javafx.scenebuilder.kit.library.util.JarExplorer.instantiateWithFXMLLoader(JarExplorer.java:83)
     at com.oracle.javafx.scenebuilder.kit.library.util.JarExplorer.exploreEntry(JarExplorer.java:117)
     at com.oracle.javafx.scenebuilder.kit.library.util.JarExplorer.explore(JarExplorer.java:43)
     at com.oracle.javafx.scenebuilder.kit.editor.panel.library.ImportWindowController$2.call(ImportWindowController.java:155)
     at com.oracle.javafx.scenebuilder.kit.editor.panel.library.ImportWindowController$2.call(ImportWindowController.java:138)
     at javafx.concurrent.Task$TaskCallable.call(Task.java:1376)
     at java.util.concurrent.FutureTask.run(FutureTask.java:262)
     at java.lang.Thread.run(Thread.java:724)
     */


    @Override
    protected void okButtonPressed(ActionEvent e) {
        getStage().close();

    }

    @Override
    protected void actionButtonPressed(ActionEvent e) {
        // NOTHING TO DO (no ACTION button)
    }

    /*
     * AbstractFxmlWindowController
     */
    @Override
    public void onCloseRequest() {
        cancelButtonPressed(null);
    }

    /*
     * AbstractWindowController
     */
    @Override
    protected void controllerDidCreateStage() {
        super.controllerDidCreateStage();
        getStage().setTitle(getI18n().getString(I18N_IMPORT_WINDOW_TITLE));
    }

    private void showErrorDialog(Exception exception) {
        dialog.showErrorAndWait(
                getI18n().getString(I18N_IMPORT_ERROR_TITLE),
                getI18n().getString(I18N_IMPORT_ERROR_MESSAGE),
                getI18n().getString(I18N_IMPORT_ERROR_DETAILS),
                exception);
    }

    void unsetProcessing() {
        processingProgressIndicator.setVisible(false);
        processingLabel.setVisible(false);
    }

    private void setProcessing() {
        cancelButton.setDefaultButton(true);
    }

    private void handleEnd(int max, int current, ProgressBox box) {
        box.unbind();
        processingProgressIndicator.setProgress((double)current/(double)max);
        tasksHolder.getChildren().remove(box);
        if (current == max) {
            closeWindow();
        }
    }
    public <R> void execute(List<Task<List<R>>> taskList) {
        this.currentTasks = (List<Task<?>>) (Object) taskList;
        ExecutorService executor = Executors.newFixedThreadPool(4);

        final AtomicInteger count = new AtomicInteger();
        taskList.stream()
            .peek(t -> {
                ProgressBox box = new ProgressBox(t);
                tasksHolder.getChildren().add(box);
                t.setOnCancelled((e) -> handleEnd(taskList.size(), count.incrementAndGet(), box));
                t.setOnFailed((e) -> handleEnd(taskList.size(), count.incrementAndGet(), box));
                t.setOnSucceeded((e) -> handleEnd(taskList.size(), count.incrementAndGet(), box));
            })
            .forEach(t -> executor.execute(t));

        executor.shutdown();
    }


    @Override
    protected void controllerDidLoadContentFxml() {
        // TODO Auto-generated method stub

    }


    @Override
    protected void cancelButtonPressed(ActionEvent e) {
        for (Task<?> task : this.currentTasks) {
            task.cancel();
        }
    }

    private class ProgressBox extends VBox {
        private Label taskLabel;
        private ProgressBar pBar;

        private ProgressBox(Task<?> task) {
            super();
            this.setAlignment(Pos.TOP_CENTER);
            taskLabel = new Label();
            pBar = new ProgressBar();
            pBar.setMaxWidth(Double.MAX_VALUE);

            pBar.progressProperty().bind(task.progressProperty());
            taskLabel.textProperty().bind(task.messageProperty());

            this.getChildren().add(pBar);
            this.getChildren().add(taskLabel);
        }

        private void unbind() {
            pBar.progressProperty().unbind();
            taskLabel.textProperty().unbind();
        }
    }
}
