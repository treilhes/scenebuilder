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
package com.oracle.javafx.scenebuilder.core.ui;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.api.subjects.SceneBuilderManager;
import com.oracle.javafx.scenebuilder.api.theme.StylesheetProvider2;
import com.oracle.javafx.scenebuilder.api.util.FxmlController;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory.SceneBuilderBeanFactoryPostProcessor;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;

import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import javafx.beans.value.ChangeListener;
import javafx.scene.Parent;

// TODO: Auto-generated Javadoc
/**
 * AbstractPanelController is the abstract base class for all the panel
 * controllers of Scene Builder Kit.
 * <p>
 * At instantiation time, each panel controller is passed a reference to its
 * editor controller which is hold in <code>editorController</code>.
 * <p>
 * Subclasses must provide three methods:
 * <ul>
 * <li>Spring will create the FX components which compose the panel and provide it using {@link FxmlController#setRoot(Parent)} called by {@link SceneBuilderBeanFactoryPostProcessor#postProcessBeanFactory(org.springframework.beans.factory.config.ConfigurableListableBeanFactory)}
 * <li><code>fxomDocumentDidChange</code> must keep the panel up to date
 * after the editor controller has changed the base document
 * <li><code>editorSelectionDidChange</code> must keep the panel up to date
 * after the editor controller has changed the selected objects.
 * </ul>
 *
 *
 */
public abstract class AbstractPanelController  {

    /** The Constant LOG. */
    private static final Logger LOG = Logger.getLogger(AbstractPanelController.class.getName());

    /** The editor controller. */
    private final Editor editorController;

    /** The panel root. */
    private Parent panelRoot;

    /** The tool stylesheet config. */
    private StylesheetProvider2 toolStylesheetConfig;

    /** The scene builder manager. */
    private final SceneBuilderManager sceneBuilderManager;

    /**
     * Base constructor for invocation by the subclasses.
     * Subclass implementations should make sure that this constructor can be
     * invoked outside of the JavaFX thread.
     *
     * @param sceneBuilderManager the scene builder manager
     * @param editor the editor controller (should not be null).
     */
    protected AbstractPanelController(SceneBuilderManager sceneBuilderManager, Editor editor) {
        assert editor != null;
        this.editorController = editor;
        this.sceneBuilderManager = sceneBuilderManager;

        startListeningToEditorSelection();
        startListeningToJobManagerRevision();
        editorController.fxomDocumentProperty().addListener((ChangeListener<FXOMDocument>) (ov, od, nd) -> {
            assert editorController.getFxomDocument() == nd;
            if (od != null) {
                od.sceneGraphRevisionProperty().removeListener(fxomDocumentRevisionListener);
                od.cssRevisionProperty().removeListener(cssRevisionListener);
            }
            try {
                fxomDocumentDidChange(od);
            } catch(RuntimeException x) {
                LOG.log(Level.SEVERE, "Bug", x); //NOI18N
            }
            if (nd != null) {
                nd.sceneGraphRevisionProperty().addListener(fxomDocumentRevisionListener);
                nd.cssRevisionProperty().addListener(cssRevisionListener);
            }
        });
        if (editorController.getFxomDocument() != null) {
            editorController.getFxomDocument().sceneGraphRevisionProperty().addListener(fxomDocumentRevisionListener);
            editorController.getFxomDocument().cssRevisionProperty().addListener(cssRevisionListener);
        }

    }

    /**
     * Returns the editor controller associated to this panel controller.
     *
     * @return the editor controller (never null).
     */
    public Editor getEditorController() {
        return editorController;
    }

    /**
     * Returns the root FX object of this panel.
     *
     * @return the root object of the panel (never null)
     */
    public Parent getRoot() {
        assert panelRoot != null;
        return panelRoot;
    }

    /*
     * To be implemented by subclasses
     */

//    /**
//     * Creates the FX object composing the panel.
//     * This routine is called by {@link AbstractPanelController#getPanelRoot}.
//     * It *must* invoke {@link AbstractPanelController#setPanelRoot}.
//     */
//    @Deprecated
//    protected abstract void makePanel();

    /**
     * Updates the panel after the editor controller has change
     * the base document. Subclass can use {@link Editor#getFxomDocument() }
     * to retrieve the newly set document (possibly null).
     *
     * @param oldDocument the previous document (possibly null).
     */
    protected abstract void fxomDocumentDidChange(FXOMDocument oldDocument);

    /**
     * Updates the panel after the revision of the scene graph has changed.
     * Revision is incremented each time the fxom document rebuilds the
     * scene graph.
     */
    protected abstract void sceneGraphRevisionDidChange();

    /**
     * Updates the panel after the css revision has changed.
     * Revision is incremented each time the fxom document forces FX to
     * reload its stylesheets.
     */
    protected abstract void cssRevisionDidChange();

    /**
     * Updates the panel after the revision of job manager has changed.
     * Revision is incremented each time a job is executed, undone or redone.
     */
    protected abstract void jobManagerRevisionDidChange();

    /**
     * Updates the panel after the editor controller has changed the selected
     * objects. Subclass can use {@link Editor#getSelection()} to
     * retrieve the currently selected objects.
     */
    protected abstract void editorSelectionDidChange();


    /*
     * For subclasses
     */

    /**
     * Set the root of this panel controller.<br>
     * This routine is invoked by {@link SceneBuilderBeanFactoryPostProcessor#postProcessBeanFactory(org.springframework.beans.factory.config.ConfigurableListableBeanFactory)}
     * using {@link FxmlController#setRoot(Parent)}
     *
     * @param panelRoot the root panel (non null).
     */
    public void setRoot(Parent panelRoot) {
        System.out.println("APC setPanelRoot " + this.getClass().getSimpleName() +  this);
        assert panelRoot != null;
        this.panelRoot = panelRoot;

        if (sceneBuilderManager != null) {
            sceneBuilderManager.stylesheetConfig()
                .subscribeOn(JavaFxScheduler.platform()).subscribe(s -> {
                toolStylesheetDidChange(s);
            });
        }
    }

    /** The fxom document revision listener. */
    private final ChangeListener<Number> fxomDocumentRevisionListener
            = (observable, oldValue, newValue) -> {
        try {
            sceneGraphRevisionDidChange();
        } catch(RuntimeException x) {
            LOG.log(Level.SEVERE, "Bug", x); //NOI18N
        }
    };

    /** The css revision listener. */
    private final ChangeListener<Number> cssRevisionListener
            = (observable, oldValue, newValue) -> {
        try {
            cssRevisionDidChange();
        } catch(RuntimeException x) {
            LOG.log(Level.SEVERE, "Bug", x); //NOI18N
        }
    };

    /** The job manager revision listener. */
    private final ChangeListener<Number> jobManagerRevisionListener
            = (observable, oldValue, newValue) -> {
        try {
            jobManagerRevisionDidChange();
        } catch(RuntimeException x) {
            LOG.log(Level.SEVERE, "Bug", x); //NOI18N
        }
    };

    /** The editor selection listener. */
    private final ChangeListener<Number> editorSelectionListener =
        (observable, oldValue, newValue) -> {
        try {
            editorSelectionDidChange();
        } catch(RuntimeException x) {
            LOG.log(Level.SEVERE, "Bug", x); //NOI18N
        }
    };

    /**
     * Setup a listener which invokes {@link #editorSelectionDidChange} each
     * time the editor controller changes the selected objects.
     * This routine is automatically called when the panel controller is
     * instantiated. Subclasses may invoke it after temporarily disabling
     * selection listening with {@link AbstractPanelController#stopListeningToEditorSelection}.
     */
    protected final void startListeningToEditorSelection() {
        editorController.getSelection().revisionProperty().addListener(editorSelectionListener);
    }

    /**
     * Removes the listener which invokes {@link #editorSelectionDidChange} each
     * time the editor controller changes the selected objects.
     * Subclasses may invoke this routine to temporarily stop listening to
     * the selection changes from the editor controller. Use
     * {@link AbstractPanelController#startListeningToEditorSelection} to
     * re-enable selection listening.
     */
    protected final void stopListeningToEditorSelection() {
        editorController.getSelection().revisionProperty().removeListener(editorSelectionListener);
    }


    /**
     * Setup a listener which invokes {@link #jobManagerRevisionDidChange} each
     * time the job manager has executed, undone or redone a job.
     * This routine is automatically called when the panel controller is
     * instantiated. Subclasses may invoke it after temporarily disabling
     * job manager listening with {@link AbstractPanelController#stopListeningToJobManagerRevision}.
     */
    protected final void startListeningToJobManagerRevision() {
        editorController.getJobManager().revisionProperty().addListener(jobManagerRevisionListener);
    }


    /**
     * Removes the listener which invokes {@link #jobManagerRevisionDidChange} each
     * time the job manager has executed, undone or redone a job.
     * Subclasses may invoke this routine to temporarily stop listening to
     * the job manager from the editor controller. Use
     * {@link AbstractPanelController#startListeningToJobManagerRevision} to
     * re-enable job manager listening.
     */
    protected final void stopListeningToJobManagerRevision() {
        editorController.getJobManager().revisionProperty().removeListener(jobManagerRevisionListener);
    }


    /**
     * Replaces old Stylesheet config by the tool style sheet assigned to this
     * controller. This methods is event binded to {@link DocumentManager#stylesheetConfig()} using an RxJava2 subscription.
     *
     * @param newToolStylesheetConfig null or the new style sheet configuration to apply
     */
    protected void toolStylesheetDidChange(StylesheetProvider2 newToolStylesheetConfig) {

        if (panelRoot == null) { // nothing to style so return
            return;
        }

        if (toolStylesheetConfig != null) { // if old conf then removeit
            panelRoot.getStylesheets().remove(toolStylesheetConfig.getUserAgentStylesheet());
            panelRoot.getStylesheets().removeAll(toolStylesheetConfig.getStylesheets());
        }

        if (newToolStylesheetConfig != null) { // replace the active conf only if the new one is valid
            toolStylesheetConfig = newToolStylesheetConfig;
        }

        //apply the conf if the current one is valid
        if (toolStylesheetConfig != null) {
            if (toolStylesheetConfig.getUserAgentStylesheet() != null) {
                panelRoot.getStylesheets().add(toolStylesheetConfig.getUserAgentStylesheet());
            }
            if (toolStylesheetConfig.getStylesheets() != null) {
                Logger.getLogger(AbstractWindowController.class.getName()).log(Level.INFO,
                        "Applying new tool theme using {0} on {1}",
                        new Object[] { toolStylesheetConfig.getStylesheets(), this.getClass().getName() });
                panelRoot.getStylesheets().addAll(toolStylesheetConfig.getStylesheets());
            }
        }
    }



}
