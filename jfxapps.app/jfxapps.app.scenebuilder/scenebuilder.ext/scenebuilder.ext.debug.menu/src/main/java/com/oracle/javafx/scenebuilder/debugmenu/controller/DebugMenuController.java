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
package com.oracle.javafx.scenebuilder.debugmenu.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.scenebuilder.fxml.api.Content;
import org.springframework.beans.factory.annotation.Autowired;

import com.gluonhq.jfxapps.boot.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.fs.FileSystem;
import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.api.job.JobManager;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationEvents;
import com.gluonhq.jfxapps.core.api.ui.dialog.Dialog;
import com.gluonhq.jfxapps.util.MathUtils;
import com.oracle.javafx.scenebuilder.api.menu.DebugMenu;
import com.oracle.javafx.scenebuilder.api.menu.DefaultMenu;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

/**
 *
 */
@ApplicationInstanceSingleton
public class DebugMenuController implements DebugMenu {

    private final Menu menu = new Menu("Debug"); //NOCHECK

    private final JobManager jobManager;
	private final FileSystem fileSystem;
	private final Dialog dialog;

    public DebugMenuController(
    		@Autowired JobManager jobManager,
    		@Autowired Content content,
    		@Autowired FileSystem fileSystem,
    		@Autowired Dialog dialog,
    		@Autowired ApplicationEvents sceneBuilderManager
    		//@Autowired @Lazy DebugMenuWindowController  debugMenuWindow
    		) {

        this.jobManager = jobManager;
        this.fileSystem = fileSystem;
        this.dialog = dialog;

        menu.setVisible(false);
        menu.setId(DefaultMenu.DEBUG_MENU_ID);
        /*
         * User Library Folder
         */
        final File applicationDataFolder = fileSystem.getApplicationDataFolder();
        final MenuItem libraryFolderMenuItem
                = new MenuItem();
        libraryFolderMenuItem.setText(applicationDataFolder.getAbsolutePath());
        libraryFolderMenuItem.setOnAction(t -> handleRevealPath(applicationDataFolder));

        final Menu libraryFolderMenu = new Menu("Application Data Folder"); //NOCHECK
        libraryFolderMenu.getItems().add(libraryFolderMenuItem);

        /*
         * Layout
         */
        final MenuItem layoutMenuItem
                = new MenuItem();
        layoutMenuItem.setText("Check \"localToSceneTransform Properties\" in Content Panel"); //NOCHECK
        layoutMenuItem.setOnAction(t -> {
            System.out.println("CHECK LOCAL TO SCENE TRANSFORM BEGINS"); //NOCHECK
            checkLocalToSceneTransform(content.getRoot());
            System.out.println("CHECK LOCAL TO SCENE TRANSFORM ENDS"); //NOCHECK
        });

//        /*
//         * Tool theme
//         */
//        final MenuItem useDefaultThemeMenuItem = new MenuItem();
//        useDefaultThemeMenuItem.setText("Use Default Theme"); //NOCHECK
//        useDefaultThemeMenuItem.setOnAction(t -> MainController.getSingleton().performControlAction(MainController.ApplicationControlAction.USE_DEFAULT_THEME,
//                DebugMenuController.this.documentWindowController));
//        final MenuItem useDarkThemeMenuItem = new MenuItem();
//        useDarkThemeMenuItem.setText("Use Dark Theme"); //NOCHECK
//        useDarkThemeMenuItem.setOnAction(t -> MainController.getSingleton().performControlAction(MainController.ApplicationControlAction.USE_DARK_THEME,
//                DebugMenuController.this.documentWindowController));

        /*
         * Undo/redo stack
         */
        final Menu undoRedoStack = new Menu();
        undoRedoStack.setText("Undo/Redo Stack"); //NOCHECK
        undoRedoStack.getItems().add(makeMenuItem("Dummy", true)); //NOCHECK
        undoRedoStack.setOnMenuValidation(t -> {
            assert t.getTarget() instanceof Menu;
            undoRedoStackMenuShowing((Menu) t.getTarget());
        });

        menu.getItems().add(libraryFolderMenu);
        menu.getItems().add(new SeparatorMenuItem());
        menu.getItems().add(layoutMenuItem);
//        menu.getItems().add(new SeparatorMenuItem());
//        menu.getItems().add(useDefaultThemeMenuItem);
//        menu.getItems().add(useDarkThemeMenuItem);
        menu.getItems().add(new SeparatorMenuItem());
        menu.getItems().add(undoRedoStack);

        sceneBuilderManager.debugMode().subscribe(debug -> {
            if (debug) {
                show();
            } else {
                hide();
            }
        });
    }

    public Menu getMenu() {
        return menu;
    }


    /*
     * Private
     */

    private void handleRevealPath(File file) {
        try {
        	fileSystem.revealInFileBrowser(file);
        } catch(IOException x) {
        	dialog.showErrorAndWait("", "Failed to reveal folder", file.getAbsolutePath(), x);
        }
    }


    private void checkLocalToSceneTransform(Node node) {

        final Point2D p1 = node.localToScene(0, 0);
        final Point2D p2 = node.getLocalToSceneTransform().transform(0, 0);

        final boolean okX = MathUtils.equals(p1.getX(), p2.getX(), 0.0000001);
        final boolean okY = MathUtils.equals(p1.getY(), p2.getY(), 0.0000001);
        if ((okX == false) || (okY == false)) {
            System.out.println("CHECK FAILED FOR " + node + ", p1=" + p1 + ", p2=" + p2); //NOCHECK
        }

        if (node instanceof Parent) {
            final Parent parent = (Parent) node;
            for (Node child : parent.getChildrenUnmodifiable()) {
                checkLocalToSceneTransform(child);
            }
        }
    }

    /*
     * Private (undo/redo stack)
     */

    private void undoRedoStackMenuShowing(Menu menu) {

        final List<Job> redoStack = jobManager.getRedoStack();
        final List<Job> undoStack = jobManager.getUndoStack();

        final List<MenuItem> menuItems = menu.getItems();

        menuItems.clear();
        if (redoStack.isEmpty()) {
            menuItems.add(makeMenuItem("Redo Stack Empty", true)); //NOCHECK
        } else {
            for (Job job : redoStack) {
                menuItems.add(0, makeJobMenuItem(job));
            }
        }

        menuItems.add(new SeparatorMenuItem());

        if (undoStack.isEmpty()) {
            menuItems.add(makeMenuItem("Undo Stack Empty", true)); //NOCHECK
        } else {
            for (Job job : undoStack) {
                menuItems.add(makeJobMenuItem(job));
            }
        }
    }


    private MenuItem makeMenuItem(String text, boolean disable) {
        final MenuItem result = new MenuItem();
        result.setText(text);
        result.setDisable(disable);
        return result;
    }


    private MenuItem makeJobMenuItem(Job job) {
        final MenuItem result;

        final var subJobs = job.getSubJobs();
        if (!subJobs.isEmpty()) {
            final Menu newMenu = new Menu(job.getClass().getSimpleName());
            addJobMenuItems(subJobs, newMenu);
            result = newMenu;
        } else {
            result = new MenuItem(job.getClass().getSimpleName());
        }

        return result;
    }

    private void addJobMenuItems(List<Job> jobs, Menu targetMenu) {
        for (Job job : jobs) {
            targetMenu.getItems().add(makeJobMenuItem(job));
        }

        if (targetMenu.getItems().isEmpty()) {
            targetMenu.getItems().add(makeMenuItem("Empty", true)); //NOCHECK
        }
    }

    @Override
    public void show() {
        menu.setVisible(true);
    }

    @Override
    public void hide() {
        menu.setVisible(false);
    }
}
