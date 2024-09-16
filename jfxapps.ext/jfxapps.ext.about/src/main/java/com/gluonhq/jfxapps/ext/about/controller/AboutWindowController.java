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
package com.gluonhq.jfxapps.ext.about.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationSingleton;
import com.gluonhq.jfxapps.core.api.About;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationEvents;
import com.gluonhq.jfxapps.core.api.ui.MainInstanceWindow;
import com.gluonhq.jfxapps.core.api.ui.controller.AbstractFxmlWindowController;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.IconSetting;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;

/**
 *
 */
@ApplicationSingleton
public class AboutWindowController extends AbstractFxmlWindowController implements About {

    private final String LOG_FILE_NAME;

    private final ApplicationEvents sceneBuilderManager;

    private String sbBuildInfo;
    private String sbBuildVersion;
    private String sbBuildDate;
    private String sbBuildJavaVersion;
    private String sbBuildJavaFXVersion;
    // The resource bundle contains two keys: about.copyright and about.copyright.open
    private String sbAboutCopyrightKeyName;
    // File name must be in sync with what we use in logging.properties (Don't understand this comment, haven't found any logging.properties file

    @FXML
    private GridPane vbox;
    @FXML
    private TextArea textArea;

    public AboutWindowController(
            I18N i18n,
            ApplicationEvents sceneBuilderManager,
            IconSetting iconSetting
            ) {
        super(i18n, sceneBuilderManager, iconSetting, AboutWindowController.class.getResource("About.fxml"));
        this.sceneBuilderManager = sceneBuilderManager;

        try (InputStream in = getClass().getResourceAsStream("about.properties")) {

            if (in != null) {
                Properties sbProps = new Properties();
                sbProps.load(in);
                sbBuildInfo = sbProps.getProperty("build.info", "UNSET");
                sbBuildVersion = sbProps.getProperty("build.version", "UNSET");
                sbBuildDate = sbProps.getProperty("build.date", "UNSET");
                sbBuildJavaVersion = sbProps.getProperty("build.java.version", "UNSET");
                sbBuildJavaFXVersion = sbProps.getProperty("build.javafx.version", "UNSET");
                sbAboutCopyrightKeyName = sbProps.getProperty("copyright.key.name", "UNSET");
            }
        } catch (IOException ex) {
            // We go with default values
        }
        this.LOG_FILE_NAME = "scenebuilder-" + sbBuildVersion + ".log"; //NOCHECK
    }

    @FXML
    public void onMousePressed(MouseEvent event) {
        if ((event.getClickCount() == 2) && event.isAltDown()) {
            boolean debug = sceneBuilderManager.debugMode().get();
            sceneBuilderManager.debugMode().set(!debug);
        }
    }

    @Override
    public void onCloseRequest() {
        closeWindow();
    }

    @Override
    public void onFocus() {}

    /*
     * AbstractWindowController
     */
    @Override
    protected void controllerDidCreateStage() {
        assert getRoot() != null;
        assert getRoot().getScene() != null;
        assert getRoot().getScene().getWindow() != null;

        getStage().setTitle(getI18n().getString("about.title"));
        getStage().initModality(Modality.APPLICATION_MODAL);
    }

    @Override
    public void controllerDidLoadFxml() {
        super.controllerDidLoadFxml();
        assert vbox != null;
        assert textArea != null;
        textArea.setText(getAboutText());
    }

    private String getAboutText() {

        StringBuilder text = getVersionParagraph()
                .append(getBuildInfoParagraph())
                .append(getLoggingParagraph())
                .append(getJavaFXParagraph())
                .append(getJavaParagraph())
                .append(getOsParagraph())
                .append(getI18n().getString(sbAboutCopyrightKeyName));

        return text.toString();
    }

    @Override
    public String getBuildJavaVersion() {
        return sbBuildJavaVersion;
    }

    @Override
    public String getBuildInfo() {
        return sbBuildInfo;
    }

    @Override
    public String getBuildVersion() {
        return sbBuildVersion;
    }

    @Override
    public String getBuildDate() {
        return sbBuildDate;
    }

    private StringBuilder getVersionParagraph() {
        StringBuilder sb = new StringBuilder(getI18n().getString("about.product.version"));
        sb.append("\nJavaFX Scene Builder ").append(sbBuildVersion) //NOCHECK
                .append("\n\n"); //NOCHECK
        return sb;
    }
    private String getLogFilePath() {
        StringBuilder sb = new StringBuilder(System.getProperty("java.io.tmpdir")); //NOCHECK
        if (sb.charAt(sb.length() - 1) != File.separatorChar) {
            sb.append(File.separatorChar);
        }
        sb.append(LOG_FILE_NAME);
        return sb.toString();

    }

    private StringBuilder getBuildInfoParagraph() {
        StringBuilder sb = new StringBuilder(getI18n().getString("about.build.information"));
        sb.append("\n").append(sbBuildInfo).append("\n") //NOCHECK
                .append(getI18n().getString("about.build.date", sbBuildDate)).append("\n")
                .append(getI18n().getString("about.build.javafx.version", sbBuildJavaFXVersion)).append("\n")
                .append(getI18n().getString("about.build.java.version", sbBuildJavaVersion))
                .append("\n\n"); //NOCHECK
        return sb;
    }

    private StringBuilder getLoggingParagraph() {
        StringBuilder sb = new StringBuilder(getI18n().getString("about.logging.title"));
        sb.append("\n") //NOCHECK
                .append(getI18n().getString("about.logging.body.first", LOG_FILE_NAME))
                .append("\n") //NOCHECK
                .append(getI18n().getString("about.logging.body.second", getLogFilePath()))
                .append("\n\n"); //NOCHECK
        return sb;
    }

    private StringBuilder getJavaFXParagraph() {
        StringBuilder sb = new StringBuilder("JavaFX\n"); //NOCHECK
        sb.append(System.getProperty("javafx.version")).append("\n\n"); //NOCHECK
        return sb;
    }

    private StringBuilder getJavaParagraph() {
        StringBuilder sb = new StringBuilder("Java\n"); //NOCHECK
        sb.append(System.getProperty("java.runtime.version")).append(", ") //NOCHECK
                .append(System.getProperty("java.vendor")).append("\n\n"); //NOCHECK
        return sb;
    }

    private StringBuilder getOsParagraph() {
        StringBuilder sb = new StringBuilder(getI18n().getString("about.operating.system"));
        sb.append("\n").append(System.getProperty("os.name")).append(", ") //NOCHECK
                .append(System.getProperty("os.arch")).append(", ") //NOCHECK
                .append(System.getProperty("os.version")).append("\n\n"); //NOCHECK
        return sb;
    }
}
