package com.oracle.javafx.scenebuilder.devutils.strchk.controller;

import com.oracle.javafx.scenebuilder.devutils.strchk.model.Project;
import com.oracle.javafx.scenebuilder.devutils.strchk.model.ProjectFile;
import com.oracle.javafx.scenebuilder.devutils.strchk.model.StringOccurence;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class ResourceLocationItem {
    private final @Getter Project project;
    private final @Getter ProjectFile projectFile;
    private final @Getter StringOccurence occurence;
    
    private @Getter @Setter Project matchProject;
    private @Getter @Setter ProjectFile matchProjectFile;
    
    private StringProperty matchProjectName = new SimpleStringProperty();
    private StringProperty matchPackageName = new SimpleStringProperty();
    private StringProperty matchFileName = new SimpleStringProperty();
    private StringProperty matchError = new SimpleStringProperty("");
    private StringProperty matchSolution = new SimpleStringProperty("");
    
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
