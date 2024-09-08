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
package com.gluonhq.jfxapps.app.devtools.ext.strchk.controller;

import com.gluonhq.jfxapps.app.devtools.ext.strchk.model.Project;
import com.gluonhq.jfxapps.app.devtools.ext.strchk.model.ProjectFile;
import com.gluonhq.jfxapps.app.devtools.ext.strchk.model.StringOccurence;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ResourceLocationItem {
    private final Project project;
    private final ProjectFile projectFile;
    private final StringOccurence occurence;

    private Project matchProject;
    private ProjectFile matchProjectFile;

    private StringProperty matchProjectName = new SimpleStringProperty();
    private StringProperty matchPackageName = new SimpleStringProperty();
    private StringProperty matchFileName = new SimpleStringProperty();
    private StringProperty matchError = new SimpleStringProperty("");
    private StringProperty matchSolution = new SimpleStringProperty("");

    public ResourceLocationItem(Project project, ProjectFile projectFile, StringOccurence occurence) {
        this.project = project;
        this.projectFile = projectFile;
        this.occurence = occurence;
    }

    public Project getProject() {
        return project;
    }

    public ProjectFile getProjectFile() {
        return projectFile;
    }

    public StringOccurence getOccurence() {
        return occurence;
    }

    public String getProjectName() {
        return project.getName();
    }

    public String getPackageName() {
        return projectFile.getPackageName();
    }

    public String getFileName() {
        return projectFile.getName();
    }

    public String getValue() {
        return occurence.getValue();
    }

    public final StringProperty matchProjectNameProperty() {
        return this.matchProjectName;
    }


    public final String getMatchProjectName() {
        return this.matchProjectNameProperty().get();
    }


    public final void setMatchProjectName(final String matchProjectName) {
        this.matchProjectNameProperty().set(matchProjectName);
    }


    public final StringProperty matchPackageNameProperty() {
        return this.matchPackageName;
    }


    public final String getMatchPackageName() {
        return this.matchPackageNameProperty().get();
    }


    public final void setMatchPackageName(final String matchPackageName) {
        this.matchPackageNameProperty().set(matchPackageName);
    }


    public final StringProperty matchFileNameProperty() {
        return this.matchFileName;
    }


    public final String getMatchFileName() {
        return this.matchFileNameProperty().get();
    }


    public final void setMatchFileName(final String matchFileName) {
        this.matchFileNameProperty().set(matchFileName);
    }

    public final StringProperty matchErrorProperty() {
        return this.matchError;
    }


    public final String getMatchError() {
        return this.matchErrorProperty().get();
    }


    public final void setMatchError(final String matchError) {
        this.matchErrorProperty().set(matchError);
    }

    public final StringProperty matchSolutionProperty() {
        return this.matchSolution;
    }


    public final String getMatchSolution() {
        return this.matchSolutionProperty().get();
    }

    public final void setMatchSolution(final String matchSolution) {
        this.matchSolutionProperty().set(matchSolution);
    }

    public Project getMatchProject() {
        return matchProject;
    }

    public void setMatchProject(Project matchProject) {
        this.matchProject = matchProject;
    }

    public ProjectFile getMatchProjectFile() {
        return matchProjectFile;
    }

    public void setMatchProjectFile(ProjectFile matchProjectFile) {
        this.matchProjectFile = matchProjectFile;
    }

    public void updateMatch(Project matchProject, ProjectFile matchfile, String name, String error) {
        setMatchProject(matchProject);
        setMatchProjectFile(matchfile);
        setMatchFileName(name);
        setMatchError(error);

        setMatchProjectName(matchProject.getName());
        setMatchPackageName(matchfile.getPackageName());
    }

    @Override
    public String toString() {
        return String.format("%s %s %s %s %s %s %s %s %s",
                getProjectName(), getPackageName(), getFileName(), getValue(),
                getMatchProjectName(), getMatchPackageName(), getMatchFileName(),
                getMatchError(), getMatchSolution());
    }


}
