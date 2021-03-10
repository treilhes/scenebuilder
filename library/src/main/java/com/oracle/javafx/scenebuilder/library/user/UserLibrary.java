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
package com.oracle.javafx.scenebuilder.library.user;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.FileSystem;
import com.oracle.javafx.scenebuilder.api.UILogger;
import com.oracle.javafx.scenebuilder.api.library.AbstractLibrary;
import com.oracle.javafx.scenebuilder.api.library.JarReport;
import com.oracle.javafx.scenebuilder.api.library.Library;
import com.oracle.javafx.scenebuilder.api.library.LibraryFilter;
import com.oracle.javafx.scenebuilder.api.library.LibraryItem;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.library.BuiltinLibrary;
import com.oracle.javafx.scenebuilder.library.BuiltinSectionComparator;
import com.oracle.javafx.scenebuilder.library.preferences.global.MavenArtifactsPreferences;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 *
 *
 */
@Component//("userLibrary")
@Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
public class UserLibrary extends AbstractLibrary implements Library, InitializingBean{

    public enum State { READY, WATCHING }

    private final BuiltinLibrary builtinLibrary;

    private final File path;
    private final BuiltinSectionComparator sectionComparator
            = new BuiltinSectionComparator();

    private final ObservableList<JarReport> jarReports = FXCollections.observableArrayList();
    private final ObservableList<JarReport> previousJarReports = FXCollections.observableArrayList();
    
    private final ObservableList<JarReport> explorationJarReports = FXCollections.observableArrayList();
    
    private final ObservableList<Path> fxmlFileReports = FXCollections.observableArrayList();
    private final ObservableList<Path> previousFxmlFileReports = FXCollections.observableArrayList();
    private final SimpleIntegerProperty explorationCountProperty = new SimpleIntegerProperty();
    private final SimpleObjectProperty<Date> explorationDateProperty = new SimpleObjectProperty<>();
    private final ReadOnlyBooleanWrapper firstExplorationCompleted = new ReadOnlyBooleanWrapper(false);
    private SimpleBooleanProperty exploring = new SimpleBooleanProperty();

    private State state = State.READY;
    private Exception exception;

    private LibraryFolderWatcher watcher;

    private Thread watcherThread;
    // Where we store canonical class names of items we want to exclude from
    // the user defined one displayed in the Library panel.
    // As a consequence an empty file means we display all items.
    private final String filterFileName = "filter.txt"; //NOI18N

    private Supplier<List<Path>> additionalJarPaths;
    private Supplier<List<String>> additionalFilter;

	private UILogger uiLogger;

	private final MavenArtifactsPreferences preferences;

    private final List<LibraryFilter> filters;

    /*
     * Public
     */
    protected UserLibrary(
            @Autowired FileSystem fileSystem,
            @Autowired MavenArtifactsPreferences mavenPreferences,
            @Autowired UILogger logger,
            @Autowired BuiltinLibrary builtinLibrary,
            @Autowired(required = false) List<LibraryFilter> filters) {
        this.path = fileSystem.getUserLibraryFolder();
        this.uiLogger = logger;
        this.builtinLibrary = builtinLibrary;
        this.preferences = mavenPreferences;
        this.filters = filters;
    }

    @Override
	public void afterPropertiesSet() throws Exception {
    	if (preferences != null) {
        	this.additionalJarPaths = () -> preferences.getArtifactsPathsWithDependencies();
            this.additionalFilter = () -> preferences.getArtifactsFilter();
        }

        explorationCountProperty().addListener((ChangeListener<Number>) (ov, t, t1) -> userLibraryExplorationCountDidChange());

        startWatching();
	}

    public void setAdditionalJarPaths(Supplier<List<Path>> additionalJarPaths)
    {
        this.additionalJarPaths = additionalJarPaths;
    }

    public void setAdditionalFilter(Supplier<List<String>> additionalFilter) {
        this.additionalFilter = additionalFilter;
    }

    @Override
    public File getPath() {
        return path;
    }

    @Override
    public ObservableList<JarReport> getJarReports() {
        return jarReports;
    }
    
    @Override
    public ObservableList<JarReport> getExplorationJarReports() {
        return explorationJarReports;
    }

    public ObservableList<JarReport> getPreviousJarReports() {
        return previousJarReports;
    }

    public ObservableList<Path> getFxmlFileReports() {
        return fxmlFileReports;
    }

    public ObservableList<Path> getPreviousFxmlFileReports() {
        return previousFxmlFileReports;
    }

    public synchronized State getState() {
        return state;
    }

    // TODO another watching code. may be replaced by filsesystem watcher
    public synchronized void startWatching() {
        assert state == State.READY;

        if (state == State.READY) {
            assert watcher == null;
            assert watcherThread == null;

            watcher = new LibraryFolderWatcher(this, builtinLibrary, filters);
            watcherThread = new Thread(watcher);
            watcherThread.setName(watcher.getClass().getSimpleName() + "(" + path  + ")"); //NOI18N
            watcherThread.setDaemon(true);
            watcherThread.start();
            state = State.WATCHING;
        }
    }

    // TODO another watching code. may be replaced by filsesystem watcher
    public synchronized void stopWatching() {
        assert state == State.WATCHING;

        if (state == State.WATCHING) {
            assert watcher != null;
            assert watcherThread != null;
            assert exception == null;

            watcherThread.interrupt();

            try {
                watcherThread.join();
            } catch(InterruptedException x) {
                x.printStackTrace();
            } finally {
                watcher = null;
                watcherThread = null;
                state = State.READY;

                // In READY state, we release the class loader.
                // This enables library import to manipulate jar files.
                changeClassLoader(null);
                previousJarReports.clear();
            }
        }
    }

    public int getExplorationCount() {
        return explorationCountProperty.get();
    }

    public ReadOnlyIntegerProperty explorationCountProperty() {
        return explorationCountProperty;
    }

    @Override
    public Date getExplorationDate() {
        return explorationDateProperty.get();
    }

    public ReadOnlyObjectProperty<Date> explorationDateProperty() {
        return explorationDateProperty;
    }

    public void setFilter(List<String> classnames) throws FileNotFoundException, IOException {
//        if (classnames != null && classnames.size() > 0) { // empty classnames means "no filter", so we need to clear filters.txt file
            File filterFile = getFilterFile();
            // TreeSet to get natural order sorting and no duplicates
            TreeSet<String> allClassnames = new TreeSet<>();

            for (String classname : classnames) {
                allClassnames.add(classname);
            }

            Path filterFilePath = getPath().toPath().resolve(filterFileName);
            Path formerFilterFilePath = getPath().toPath().resolve(filterFileName + ".tmp"); //NOI18N
            Files.deleteIfExists(formerFilterFilePath);

            try {
                // Rename already existing filter file so that we can rollback
                if (Files.exists(filterFilePath)) {
                    Files.move(filterFilePath, formerFilterFilePath, StandardCopyOption.ATOMIC_MOVE);
                }

                // Create the new filter file
                Files.createFile(filterFilePath);

                // Write content of the new filter file
                try (PrintWriter writer = new PrintWriter(filterFile, "UTF-8")) { //NOI18N
                    for (String classname : allClassnames) {
                        writer.write(classname + "\n"); //NOI18N
                    }
                }

                // Delete the former filter file
                if (Files.exists(formerFilterFilePath)) {
                    Files.delete(formerFilterFilePath);
                }
            } catch (IOException ioe) {
                // Rollback
                if (Files.exists(formerFilterFilePath)) {
                    Files.move(formerFilterFilePath, filterFilePath, StandardCopyOption.ATOMIC_MOVE);
                }
                throw (ioe);
            }
//        }
    }

    public List<String> getFilter() throws FileNotFoundException, IOException {
        List<String> res = new ArrayList<>();
        File filterFile =  getFilterFile();

        if (filterFile.exists()) {
            try (LineNumberReader reader = new LineNumberReader(new InputStreamReader(new FileInputStream(filterFile), "UTF-8"))) { //NOI18N
                String line;
                while ((line = reader.readLine()) != null) {
                    res.add(line);
                }
            }
        }

        return res;
    }

    @Override
    public void setOnUpdatedJarReports(Consumer<List<? extends JarReport>> onFinishedUpdatingJarReports) {
        if (this.jarReports.size() > 0) {
        	onFinishedUpdatingJarReports.accept(this.jarReports);
        } else {
        	this.jarReports.addListener(new ListChangeListener<JarReport>() {

				@Override
				public void onChanged(Change<? extends JarReport> c) {
					while (c.next()) {
						onFinishedUpdatingJarReports.accept(c.getAddedSubList());
					}
				}

        	});
        }
    }

    @Override
    public void setOnUpdatedExploringJarReports(Consumer<List<? extends JarReport>> onFinishedExploringJarReports) {
        if (this.explorationJarReports.size() > 0) {
            onFinishedExploringJarReports.accept(this.explorationJarReports);
        } else {
            this.explorationJarReports.addListener(new ListChangeListener<JarReport>() {
                @Override
                public void onChanged(Change<? extends JarReport> c) {
                    while (c.next()) {
                        onFinishedExploringJarReports.accept(c.getAddedSubList());
                    }
                }
            });
        }
    }
    
    @Override
    public final ReadOnlyBooleanProperty firstExplorationCompletedProperty() {
        return firstExplorationCompleted.getReadOnlyProperty();
    }

    public final boolean isFirstExplorationCompleted() {
        return firstExplorationCompleted.get();
    }

    @Override
    public SimpleBooleanProperty exploringProperty() {
        return exploring;
    }

    public boolean isExploring() {
        return exploringProperty().get();
    }

    public void setExploring(boolean value) {
        if (Platform.isFxApplicationThread())
            exploringProperty().set(value);
        else
            Platform.runLater(() -> setExploring(value));
    }


    /*
     * Package
     */

    File getFilterFile() {
        return new File(getPath(), filterFileName);
    }

    void updateJarReports(Collection<JarReport> newJarReports) {
        previousJarReports.setAll(jarReports);
        jarReports.setAll(newJarReports);
    }

    void updateFxmlFileReports(Collection<Path> newFxmlFileReports) {
        if (Platform.isFxApplicationThread()) {
            previousFxmlFileReports.setAll(fxmlFileReports);
            fxmlFileReports.setAll(newFxmlFileReports);
        } else {
            Platform.runLater(() -> {
                previousFxmlFileReports.setAll(fxmlFileReports);
                fxmlFileReports.setAll(newFxmlFileReports);
            });
        }
    }

    void setItems(Collection<LibraryItem> items) {
        if (Platform.isFxApplicationThread()) {
            itemsProperty.setAll(items);
        } else {
            Platform.runLater(() -> {
            	itemsProperty.setAll(items);
            });
        }
    }

    void addItems(Collection<LibraryItem> items) {
        if (Platform.isFxApplicationThread()) {
            itemsProperty.addAll(items);
        } else {
            Platform.runLater(() -> itemsProperty.addAll(items));
        }
    }

    void updateClassLoader(ClassLoader newClassLoader) {
        if (Platform.isFxApplicationThread()) {
            changeClassLoader(newClassLoader);
        } else {
            Platform.runLater(() -> changeClassLoader(newClassLoader));
        }
    }

    void updateExplorationCount(int count) {
        if (Platform.isFxApplicationThread()) {
            explorationCountProperty.set(count);
        } else {
            Platform.runLater(() -> explorationCountProperty.set(count));
        }
    }

    void updateExplorationDate(Date date) {
        if (Platform.isFxApplicationThread()) {
            explorationDateProperty.set(date);
        } else {
            Platform.runLater(() -> explorationDateProperty.set(date));
        }
    }

    void updateFirstExplorationCompleted() {
        if (Platform.isFxApplicationThread()) {
            firstExplorationCompleted.set(true);
        } else {
            Platform.runLater(() -> firstExplorationCompleted.set(true));
        }
    }

    Supplier<List<Path>> getAdditionalJarPaths() {
        return additionalJarPaths;
    }

    Supplier<List<String>> getAdditionalFilter() {
        return additionalFilter;
    }

//    Consumer<List<JarReport>> getOnFinishedUpdatingJarReports() {
//        return onFinishedUpdatingJarReports;
//    }

    /*
     * Library
     */
    @Override
    public Comparator<String> getSectionComparator() {
        return sectionComparator;
    }

    /*
     * Private
     */

    private void changeClassLoader(ClassLoader newClassLoader) {
        assert Platform.isFxApplicationThread();

        /*
         * Before changing to the new class loader,
         * we invoke URLClassLoader.close() on the existing one
         * so that it releases its associated jar files.
         */
        final ClassLoader classLoader = classLoaderProperty.get();
        if (classLoader instanceof URLClassLoader) {
            final URLClassLoader urlClassLoader = (URLClassLoader) classLoader;
            try {
                urlClassLoader.close();
            } catch(IOException x) {
                x.printStackTrace();
            }
        }

        // Now moves to the new class loader
        classLoaderProperty.set(newClassLoader);
    }

    private void userLibraryExplorationCountDidChange() {
        // We can have 0, 1 or N FXML file, same for JAR one.
        final int numOfFxmlFiles = getFxmlFileReports().size();
        final int numOfJarFiles = getJarReports().size();
        final int jarCount = getJarReports().size();
        final int fxmlCount = getFxmlFileReports().size();

        switch (numOfFxmlFiles + numOfJarFiles) {
            case 0: // Case 0-0
                final int previousNumOfJarFiles = getPreviousJarReports().size();
                final int previousNumOfFxmlFiles = getPreviousFxmlFileReports().size();
                if (previousNumOfFxmlFiles > 0 || previousNumOfJarFiles > 0) {
                    uiLogger.logInfoMessage("log.user.exploration.0");
                }
                break;
            case 1:
                Path path;
                if (numOfFxmlFiles == 1) { // Case 1-0
                    path = getFxmlFileReports().get(0);
                } else { // Case 0-1
                    path = getJarReports().get(0).getJar();
                }
                uiLogger.logInfoMessage("log.user.exploration.1", path.getFileName());
                break;
            default:
                switch (numOfFxmlFiles) {
                    case 0: // Case 0-N
                    	uiLogger.logInfoMessage("log.user.jar.exploration.n", jarCount);
                        break;
                    case 1:
                        final Path fxmlName = getFxmlFileReports().get(0).getFileName();
                        if (numOfFxmlFiles == numOfJarFiles) { // Case 1-1
                            final Path jarName = getJarReports().get(0).getJar().getFileName();
                            uiLogger.logInfoMessage("log.user.fxml.jar.exploration.1.1", fxmlName, jarName);
                        } else { // Case 1-N
                        	uiLogger.logInfoMessage("log.user.fxml.jar.exploration.1.n", fxmlName, jarCount);
                        }
                        break;
                    default:
                        switch (numOfJarFiles) {
                            case 0: // Case N-0
                            	uiLogger.logInfoMessage("log.user.fxml.exploration.n", fxmlCount);
                                break;
                            case 1: // Case N-1
                                final Path jarName = getJarReports().get(0).getJar().getFileName();
                                uiLogger.logInfoMessage("log.user.fxml.jar.exploration.n.1", fxmlCount, jarName);
                                break;
                            default: // Case N-N
                            	uiLogger.logInfoMessage("log.user.fxml.jar.exploration.n.n", fxmlCount, jarCount);
                                break;
                        }
                        break;
                }
                break;
        }
    }

    @Override
    public List<LibraryFilter> getFilters() {
        return filters;
    }

    
    
}