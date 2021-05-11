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
package com.oracle.javafx.scenebuilder.controllibrary.tmp;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.UILogger;
import com.oracle.javafx.scenebuilder.api.library.LibraryFilter;
import com.oracle.javafx.scenebuilder.api.lifecycle.DisposeWithSceneBuilder;
import com.oracle.javafx.scenebuilder.api.subjects.SceneBuilderManager;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.controllibrary.ControlLibraryExtension;
import com.oracle.javafx.scenebuilder.controllibrary.aaa.AbstractLibrary;
import com.oracle.javafx.scenebuilder.controllibrary.aaa.Explorer;
import com.oracle.javafx.scenebuilder.controllibrary.aaa.LibraryDialogFactory;
import com.oracle.javafx.scenebuilder.controllibrary.aaa.LibraryStoreFactory;
import com.oracle.javafx.scenebuilder.controllibrary.editor.panel.explorer.ControlFileExplorer;
import com.oracle.javafx.scenebuilder.controllibrary.editor.panel.explorer.ControlFolderExplorer;
import com.oracle.javafx.scenebuilder.controllibrary.editor.panel.explorer.ControlMavenArtifactExplorer;
import com.oracle.javafx.scenebuilder.controllibrary.editor.panel.library.ImportWindowController;
import com.oracle.javafx.scenebuilder.controllibrary.tobeclassed.BuiltinLibrary;
import com.oracle.javafx.scenebuilder.controllibrary.tobeclassed.LibraryItemImpl;
import com.oracle.javafx.scenebuilder.core.metadata.klass.ComponentClassMetadata.Qualifier;
import com.oracle.javafx.scenebuilder.extstore.fs.ExtensionFileSystemFactory;
import com.oracle.javafx.scenebuilder.fs.controller.ClassLoaderController;
import com.oracle.javafx.scenebuilder.library.editor.panel.library.LibraryUtil;
import com.oracle.javafx.scenebuilder.library.editor.panel.library.maven.MavenArtifact;
import com.oracle.javafx.scenebuilder.library.util.Transform;

/**
 *
 *
 */
@Component//("userLibrary")
@Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
@DependsOn("metadata")
public class ControlLibrary extends AbstractLibrary<ControlReportImpl, LibraryItemImpl> implements InitializingBean, DisposeWithSceneBuilder{
    
    private final static Logger logger = LoggerFactory.getLogger(ControlLibrary.class);

    private final static String LIBRARY_ID = "Control";
    
    private final BuiltinLibrary builtinLibrary;
 
    

    //private LibraryStoreWatcher watcher;

    // Where we store canonical class names of items we want to exclude from
    // the user defined one displayed in the Library panel.
    // As a consequence an empty file means we display all items.
    private final String filterFileName = "filter.txt"; //NOI18N

//    private Supplier<List<Path>> additionalJarPaths;
//    private Supplier<List<String>> additionalFilter;

	private UILogger uiLogger;

	//private final MavenArtifactsPreferences preferences;

    private final List<LibraryFilter> filters;

    private final ApplicationContext context;

    private final ControlFileExplorer controlFileExplorer;

    private final ControlFolderExplorer controlFolderExplorer;

    private final ControlMavenArtifactExplorer controlMavenArtifactExplorer;

    private final ClassLoaderController classLoaderController;

    //private Transform<ControlReport, ControlReport> controlFilter;
    private ControlFilterTransform controlFilter;

    //private ClassLoaderController classLoaderController;

    /*
     * Public
     */
    protected ControlLibrary(
            @Autowired ApplicationContext context,
            @Autowired BuiltinLibrary builtinLibrary,
            @Autowired ControlLibraryDialogConfiguration libraryDialogConfiguration,
            @Autowired ExtensionFileSystemFactory extFactory,
            @Autowired LibraryDialogFactory libraryDialogFactory,
            @Autowired LibraryStoreFactory libraryStoreFactory,
            @Autowired ClassLoaderController classLoaderController,
            @Autowired UILogger logger,
            @Autowired SceneBuilderManager sceneBuilderManager,
            @Autowired ControlFileExplorer controlFileExplorer,
            @Autowired ControlFolderExplorer controlFolderExplorer,
            @Autowired ControlMavenArtifactExplorer controlMavenArtifactExplorer,
            @Autowired(required = false) List<LibraryFilter> filters) {
        super(context,sceneBuilderManager,
                classLoaderController,
                libraryStoreFactory.getStore(LIBRARY_ID, extFactory.get(ControlLibraryExtension.class)),
                libraryDialogConfiguration
                );
        this.context = context;
        this.classLoaderController = classLoaderController;
        this.uiLogger = logger;
        this.builtinLibrary = builtinLibrary;
        this.filters = filters;
        
        this.controlFileExplorer = controlFileExplorer;
        this.controlFolderExplorer = controlFolderExplorer;
        this.controlMavenArtifactExplorer = controlMavenArtifactExplorer;
    }
    
    @Override
    public String getLibraryId() {
        return LIBRARY_ID;
    }

    @Override
	public void afterPropertiesSet() throws Exception {

        getItems().addAll(builtinLibrary.getItems());
        
	}
    
    @Override
    public Explorer<MavenArtifact, ControlReportImpl> newArtifactExplorer(){
        return controlMavenArtifactExplorer;
    }
    @Override
    public Explorer<Path, ControlReportImpl> newFolderExplorer(){
        return controlFolderExplorer;
    }
    @Override
    public Explorer<Path, ControlReportImpl> newFileExplorer(){
        return controlFileExplorer;
    }
    
    @Override
    public List<ControlReportImpl> createApplyAndSaveFilter(List<ControlReportImpl> reports){
        
        ImportWindowController importWindow = context.getBean(ImportWindowController.class);
        
        try {
            if (getFilterFile().exists()) {
                controlFilter = Transform.read(getFilterFile());
            }
        } catch (IOException e) {
            logger.error("Unable to load the control library filter!", e);
        }
        if (controlFilter == null) {
            controlFilter = new ControlFilterTransform();
        }
        
        List<Path> sources = reports.stream().map(r -> r.getSource()).collect(Collectors.toList());
        
        try(URLClassLoader classLoader = classLoaderController.copyClassLoader(sources)){
            controlFilter = importWindow.editTransform(reports, controlFilter, classLoader);
        } catch(IOException e) {
            logger.error("Unable to create a copy of classloader", e);
        }
        
        
        if (controlFilter == null) { // import canceled
            return null;
        }
                
        if (controlFilter == null && getFilterFile().exists()) {
            getFilterFile().delete();
        } else {
            try {
                Transform.write(getFilterFile(), controlFilter);
            } catch (IOException e) {
                logger.error("Unable to save the control library filter!", e);
            }
        }
        
        return applySavedFilter(reports);
    }
    
    @Override
    public List<ControlReportImpl> applySavedFilter(List<ControlReportImpl> reports){
        
        try {
            if (controlFilter == null) {
                if (getFilterFile().exists()) {
                    controlFilter = Transform.read(getFilterFile());
                } else {
                    controlFilter = new ControlFilterTransform();
                }
            }
            return controlFilter.filter(reports);
        } catch (IOException e) {
            logger.error("Unable to load/apply control filter, returning unfiltered list", e);
        }
        return reports;
    }
    
    @Override
    protected void updateItems(Collection<LibraryItemImpl> items) {
        
        Collection<LibraryItemImpl> newItems = new ArrayList<>(items);
        newItems.addAll(builtinLibrary.getItems());
        setItems(newItems);
        
    }

    @Override
    protected Collection<LibraryItemImpl> makeLibraryItems(ControlReportImpl reports) throws IOException {
        final List<LibraryItemImpl> result = new ArrayList<>();
        //final URL iconURL = ImageUtils.getNodeIconURL(null);
        //final List<String> excludedItems = getFilter();
        //final List<String> artifactsFilter = getAdditionalFilter() != null ? getAdditionalFilter().get() : Collections.emptyList();

        boolean isFxml = LibraryUtil.isFxmlPath(reports.getSource());
        for (ControlReportEntryImpl e : reports.getEntries()) {
            if ((e.getStatus() == ControlReportEntryImpl.Status.OK) && e.isNode()) {
                if (isFxml) {
                    String fileName = reports.getSource().getFileName().toString();
                    String itemName = fileName.substring(0, fileName.indexOf(".fxml")); //NOI18N
                    String fxmlText = Files.readString(reports.getSource(), StandardCharsets.UTF_8);
                    result.add(new LibraryItemImpl(itemName, Qualifier.UNKNOWN, fxmlText));
                } else {
                    // We filter out items listed in the excluded list, based on canonical name of the class.
//                    final String canonicalName = e.getKlass().getCanonicalName();
//                    if (!excludedItems.contains(canonicalName) &&
//                        !artifactsFilter.contains(canonicalName)) {
                        final String name = e.getKlass().getSimpleName();
                        final String fxmlText = LibraryUtil.makeFxmlText(e.getKlass());
                        result.add(new LibraryItemImpl(name, Qualifier.UNKNOWN, fxmlText));
//                    }
                }
            }
            
        }

        return result;
    }


    /*
     * Package
     */

    File getFilterFile() {
        return new File(getPath(), filterFileName);
    }


    @Override
    protected void userLibraryExplorationDidChange(Exploration<ControlReportImpl> previous, Exploration<ControlReportImpl> current) {
        
        Map<Boolean, List<ControlReportImpl>> partitioned = current.getReports().stream().collect(Collectors.partitioningBy(r -> LibraryUtil.isFxmlPath(r.getSource())));
        List<ControlReportImpl> currentFxmlReports = partitioned.get(true);
        List<ControlReportImpl> currentJarReports = partitioned.get(false);
        
        
        // We can have 0, 1 or N FXML file, same for JAR one.
        final int numOfFxmlFiles = currentFxmlReports.size();
        final int numOfJarFiles = currentJarReports.size();

        switch (numOfFxmlFiles + numOfJarFiles) {
            case 0: // Case 0-0
                
                Map<Boolean, List<ControlReportImpl>> previousPartitioned = previous.getReports().stream().collect(Collectors.partitioningBy(r -> LibraryUtil.isFxmlPath(r.getSource())));
                List<ControlReportImpl> previousFxmlReports = previousPartitioned.get(true);
                List<ControlReportImpl> previousJarReports = previousPartitioned.get(false);
                
                final int previousNumOfJarFiles = previousJarReports.size();
                final int previousNumOfFxmlFiles = previousFxmlReports.size();
                if (previousNumOfFxmlFiles > 0 || previousNumOfJarFiles > 0) {
                    uiLogger.logInfoMessage("log.user.exploration.0");
                }
                break;
            case 1:
                Path path;
                if (numOfFxmlFiles == 1) { // Case 1-0
                    path = currentFxmlReports.get(0).getSource();
                } else { // Case 0-1
                    path = currentJarReports.get(0).getSource();
                }
                uiLogger.logInfoMessage("log.user.exploration.1", path.getFileName());
                break;
            default:
                switch (numOfFxmlFiles) {
                    case 0: // Case 0-N
                    	uiLogger.logInfoMessage("log.user.jar.exploration.n", numOfJarFiles);
                        break;
                    case 1:
                        final Path fxmlName = currentFxmlReports.get(0).getSource().getFileName();
                        if (numOfFxmlFiles == numOfJarFiles) { // Case 1-1
                            final Path jarName = currentJarReports.get(0).getSource().getFileName();
                            uiLogger.logInfoMessage("log.user.fxml.jar.exploration.1.1", fxmlName, jarName);
                        } else { // Case 1-N
                        	uiLogger.logInfoMessage("log.user.fxml.jar.exploration.1.n", fxmlName, numOfJarFiles);
                        }
                        break;
                    default:
                        switch (numOfJarFiles) {
                            case 0: // Case N-0
                            	uiLogger.logInfoMessage("log.user.fxml.exploration.n", numOfFxmlFiles);
                                break;
                            case 1: // Case N-1
                                final Path jarName = currentJarReports.get(0).getSource().getFileName();
                                uiLogger.logInfoMessage("log.user.fxml.jar.exploration.n.1", numOfFxmlFiles, jarName);
                                break;
                            default: // Case N-N
                            	uiLogger.logInfoMessage("log.user.fxml.jar.exploration.n.n", numOfFxmlFiles, numOfJarFiles);
                                break;
                        }
                        break;
                }
                break;
        }
    }
    

    @Override
    public void unlock(List<Path> pathes) {
        try {
            classLoaderController.releaseClassLoader();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void lock(List<Path> pathes) {
        try {
            classLoaderController.updateClassLoader();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public List<LibraryFilter> getFilters() {
        return filters;
    }

    @Override
    public void dispose() {
        stopWatching();
    }
}
