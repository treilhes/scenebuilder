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
package com.oracle.javafx.scenebuilder.imagelibrary.library;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.graalvm.compiler.lir.CompositeValue.Component;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;

import com.gluonhq.jfxapps.core.api.library.LibraryFilter;
import com.gluonhq.jfxapps.core.api.subjects.SceneBuilderManager;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.UILogger;
import com.gluonhq.jfxapps.core.extstore.fs.ExtensionFileSystemFactory;
import com.gluonhq.jfxapps.core.fs.controller.ClassLoaderController;
import com.gluonhq.jfxapps.core.library.api.AbstractLibrary;
import com.gluonhq.jfxapps.core.library.api.AbstractLibrary.Exploration;
import com.gluonhq.jfxapps.core.library.api.Explorer;
import com.gluonhq.jfxapps.core.library.api.LibraryDialogFactory;
import com.gluonhq.jfxapps.core.library.api.LibraryStoreFactory;
import com.gluonhq.jfxapps.core.library.api.Transform;
import com.gluonhq.jfxapps.core.library.maven.MavenArtifact;
import com.gluonhq.jfxapps.core.library.util.LibraryUtil;
import com.oracle.javafx.scenebuilder.imagelibrary.ImageLibraryExtension;
import com.oracle.javafx.scenebuilder.imagelibrary.importer.ImageImportWindowController;
import com.oracle.javafx.scenebuilder.imagelibrary.library.ImageReportEntry.Type;
import com.oracle.javafx.scenebuilder.imagelibrary.library.builtin.ImageBuiltinLibrary;
import com.oracle.javafx.scenebuilder.imagelibrary.library.builtin.LibraryItemImpl;
import com.oracle.javafx.scenebuilder.imagelibrary.library.explorer.ImageExplorerUtil;
import com.oracle.javafx.scenebuilder.imagelibrary.library.explorer.ImageFileExplorer;
import com.oracle.javafx.scenebuilder.imagelibrary.library.explorer.ImageFolderExplorer;
import com.oracle.javafx.scenebuilder.imagelibrary.library.explorer.ImageMavenArtifactExplorer;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 *
 *
 */
@Component//("userLibrary") //NOCHECK
@Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
@DependsOn("metadata") //NOCHECK
public class ImageLibrary extends AbstractLibrary<ImageReport, LibraryItemImpl> implements InitializingBean, DisposeWithApplication{

    public final static String TTF_EXTENSION = "ttf"; //NOCHECK
    //public final static String OTF_EXTENSION = "otf"; //NOCHECK
    public final static List<String> HANDLED_JAVA_EXTENSIONS = List.of("jar"); //NOCHECK
    public final static List<String> HANDLED_IMAGE_EXTENSIONS = List.of("jpg", "jpeg", "gif", "png", "ttf");//, "otf"); //NOCHECK
    public final static List<String> HANDLED_FILE_EXTENSIONS = List.of("jar", "jpg", "jpeg", "gif", "png", "ttf");//, "otf"); //NOCHECK

    private final static Logger logger = LoggerFactory.getLogger(ImageLibrary.class);

    private final static String LIBRARY_ID = "Images"; //NOCHECK

    private final ImageBuiltinLibrary builtinLibrary;



    //private LibraryStoreWatcher watcher;

    // Where we store canonical class names of items we want to exclude from
    // the user defined one displayed in the Library panel.
    // As a consequence an empty file means we display all items.
    private final String filterFileName = "filter.txt"; //NOCHECK

//    private Supplier<List<Path>> additionalJarPaths;
//    private Supplier<List<String>> additionalFilter;

	private UILogger uiLogger;

	//private final MavenArtifactsPreferences preferences;

    private final List<LibraryFilter> filters;

    private final SceneBuilderBeanFactory context;

    private final ImageFileExplorer controlFileExplorer;

    private final ImageFolderExplorer controlFolderExplorer;

    private final ImageMavenArtifactExplorer controlMavenArtifactExplorer;

    private final ClassLoaderController classLoaderController;

    //private Transform<ImageReport, ImageReport> controlFilter;
    private ImageFilterTransform controlFilter;

    private final List<String> loadedFonts = new ArrayList<>();

    private final SceneBuilderManager sceneBuilderManager;

    /*
     * Public
     */
    protected ImageLibrary(
            @Autowired SceneBuilderBeanFactory context,
            @Autowired ImageBuiltinLibrary builtinLibrary,
            @Autowired ImageLibraryDialogConfiguration libraryDialogConfiguration,
            @Autowired ExtensionFileSystemFactory extFactory,
            @Autowired LibraryDialogFactory libraryDialogFactory,
            @Autowired LibraryStoreFactory libraryStoreFactory,
            @Autowired ClassLoaderController classLoaderController,
            @Autowired UILogger logger,
            @Autowired SceneBuilderManager sceneBuilderManager,
            @Autowired ImageFileExplorer controlFileExplorer,
            @Autowired ImageFolderExplorer controlFolderExplorer,
            @Autowired ImageMavenArtifactExplorer controlMavenArtifactExplorer,
            @Autowired(required = false) List<LibraryFilter> filters) {
        super(context,sceneBuilderManager,
                classLoaderController,
                libraryStoreFactory.getStore(LIBRARY_ID, extFactory.get(ImageLibraryExtension.class)),
                libraryDialogConfiguration
                );
        this.context = context;
        this.classLoaderController = classLoaderController;
        this.uiLogger = logger;
        this.builtinLibrary = builtinLibrary;
        this.filters = filters;
        this.sceneBuilderManager = sceneBuilderManager;

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
    public Explorer<MavenArtifact, ImageReport> newArtifactExplorer(){
        return controlMavenArtifactExplorer;
    }
    @Override
    public Explorer<Path, ImageReport> newFolderExplorer(){
        return controlFolderExplorer;
    }
    @Override
    public Explorer<Path, ImageReport> newFileExplorer(){
        return controlFileExplorer;
    }

    @Override
    public List<ImageReport> createApplyAndSaveFilter(List<ImageReport> reports){

        ImageImportWindowController importWindow = context.getBean(ImageImportWindowController.class);

        try {
            if (getFilterFile().exists()) {
                controlFilter = Transform.read(getFilterFile());
            }
        } catch (IOException e) {
            logger.error("Unable to load the control library filter!", e);
        }
        if (controlFilter == null) {
            controlFilter = new ImageFilterTransform();
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
    public List<ImageReport> applySavedFilter(List<ImageReport> reports){

        try {
            if (controlFilter == null) {
                if (getFilterFile().exists()) {
                    controlFilter = Transform.read(getFilterFile());
                } else {
                    controlFilter = new ImageFilterTransform();
                }
            }
            return controlFilter.filter(reports);
        } catch (IOException e) {
            logger.error("Unable to load/apply control filter, returning unfiltered list", e);
        }
        return reports;
    }

    @Override
    protected void resetBeforeUpdate() {
        loadedFonts.clear();
    }

    @Override
    protected Collection<LibraryItemImpl> makeLibraryItems(ImageReport reports) throws IOException {
        final List<LibraryItemImpl> result = new ArrayList<>();
        //final URL iconURL = ImageUtils.getNodeIconURL(null);
        //final List<String> excludedItems = getFilter();
        //final List<String> artifactsFilter = getAdditionalFilter() != null ? getAdditionalFilter().get() : Collections.emptyList();

        for (ImageReportEntry e : reports.getEntries()) {

            if ((e.getStatus() == ImageReportEntry.Status.OK)) {
                if (e.getType() == Type.FONT_ICONS && e.getUnicodePoints().size() == 1) {

                    // we need to load the font
                    if (e.getResourceName() != null) {// the font is in the classpath
                        if (!loadedFonts.contains(e.getResourceName())) {
                            try(InputStream is = sceneBuilderManager.classloader().get().getResourceAsStream(e.getResourceName());){
                                Font.loadFont(is, 0);
                                loadedFonts.add(e.getResourceName());
                            }
                        }

                    } else {
                        if (!loadedFonts.contains(reports.getSource().toString())) {
                            try(InputStream is = new FileInputStream(reports.getSource().toFile());){
                                Font.loadFont(is, 0);
                                loadedFonts.add(reports.getSource().toString());
                            }
                        }
                    }

                    String xmlEntity = ImageExplorerUtil.unicodePointToXmlEntity(e.getUnicodePoints().get(0));
                    //&#x0644;
                    //String fxmlText = makeTextText(e.getFontName(), Character.toString(e.getUnicodePoints().get(0)));
                    String fxmlText = makeTextText(e.getFontName(), xmlEntity);
                    result.add(new LibraryItemImpl(xmlEntity, e.getFontName(), fxmlText));
                } else {
                    final String fxmlText = makeImageViewText(e.getResourceName());

                    String section = "Miscellaneous";

                    if (LibraryUtil.isJarPath(reports.getSource()) || !reports.getSource().startsWith(getStore().getFilesFolder())) {
                        section = reports.getSource().getFileName().toString();
                    }
                    result.add(new LibraryItemImpl(e.getName(), section, fxmlText));
                }
            }

        }
        getStore().getConfiguration().put("fonts", String.join(",", loadedFonts.toArray(new String[0])));
        return result;
    }

    @Override
    protected void updateItems(Collection<LibraryItemImpl> items) {

        Collection<LibraryItemImpl> newItems = new ArrayList<>(items);
        newItems.addAll(builtinLibrary.getItems());
        setItems(newItems);

    }


    /*
     * Package
     */

    File getFilterFile() {
        return new File(getPath(), filterFileName);
    }


    @Override
    protected void userLibraryExplorationDidChange(Exploration<ImageReport> previous, Exploration<ImageReport> current) {

        Map<Boolean, List<ImageReport>> partitioned = current.getReports().stream().collect(Collectors.partitioningBy(r -> LibraryUtil.isFxmlPath(r.getSource())));
        List<ImageReport> currentFxmlReports = partitioned.get(true);
        List<ImageReport> currentJarReports = partitioned.get(false);


        // We can have 0, 1 or N FXML file, same for JAR one.
        final int numOfFxmlFiles = currentFxmlReports.size();
        final int numOfJarFiles = currentJarReports.size();

        switch (numOfFxmlFiles + numOfJarFiles) {
            case 0: // Case 0-0

                Map<Boolean, List<ImageReport>> previousPartitioned = previous.getReports().stream().collect(Collectors.partitioningBy(r -> LibraryUtil.isFxmlPath(r.getSource())));
                List<ImageReport> previousFxmlReports = previousPartitioned.get(true);
                List<ImageReport> previousJarReports = previousPartitioned.get(false);

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

    public static String makeImageViewText(String resourceName) {
        final StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"); // NOI18N
        sb.append("<?import "); // NOI18N
        sb.append(ImageView.class.getCanonicalName());
        sb.append("?>"); // NOI18N
        sb.append("<?import "); // NOI18N
        sb.append(Image.class.getCanonicalName());
        sb.append("?>"); // NOI18N
        sb.append("<"); // NOI18N
        sb.append(ImageView.class.getSimpleName());
        sb.append(" pickOnBounds=\"true\" preserveRatio=\"true\">");
        sb.append("<"); // NOI18N
        sb.append(Image.class.getSimpleName().toLowerCase());
        sb.append(">"); // NOI18N

        sb.append("<"); // NOI18N
        sb.append(Image.class.getSimpleName());
        sb.append(" url=\"@/");
        sb.append(resourceName);
        sb.append("\" />");

        sb.append("</"); // NOI18N
        sb.append(Image.class.getSimpleName().toLowerCase());
        sb.append(">"); // NOI18N
        sb.append("</"); // NOI18N
        sb.append(ImageView.class.getSimpleName());
        sb.append(">\n");

        return sb.toString();
    }

    public static String makeTextText(String fontName, String content) {
        final StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"); // NOI18N
        sb.append("<?import "); // NOI18N
        sb.append(Text.class.getCanonicalName());
        sb.append("?>"); // NOI18N
        sb.append("<?import "); // NOI18N
        sb.append(Font.class.getCanonicalName());
        sb.append("?>"); // NOI18N
        sb.append("<"); // NOI18N
        sb.append(Text.class.getSimpleName());
        sb.append(" text=\"");
        sb.append(content);
        sb.append("\">");
        sb.append("<"); // NOI18N
        sb.append(Font.class.getSimpleName().toLowerCase());
        sb.append(">"); // NOI18N

        sb.append("<"); // NOI18N
        sb.append(Font.class.getSimpleName());
        sb.append(" name=\"");
        sb.append(fontName);
        sb.append("\" size=\"36.0\" />");

        sb.append("</"); // NOI18N
        sb.append(Font.class.getSimpleName().toLowerCase());
        sb.append(">"); // NOI18N
        sb.append("</"); // NOI18N
        sb.append(Text.class.getSimpleName());
        sb.append(">\n");

        return sb.toString();
    }

    @Override
    public void init() {
        super.init();
        Properties props = getStore().getConfiguration();

        if (props != null && props.containsKey("fonts")) {
            String fonts = props.getProperty("fonts");
            if (fonts != null && !fonts.isEmpty()) {
                String[] fontsArray = fonts.trim().split(",");
                for (String font : fontsArray) {
                    try(InputStream is = sceneBuilderManager.classloader().get().getResourceAsStream(font);){
                        Font.loadFont(is, 0);
                    } catch (Exception e) {
                        try(InputStream is = new FileInputStream(font)){
                            Font.loadFont(is, 0);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }

        }


    }


}
