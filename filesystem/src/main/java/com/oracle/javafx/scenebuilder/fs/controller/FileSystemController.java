package com.oracle.javafx.scenebuilder.fs.controller;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.FileSystem;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.action.editor.EditorPlatform;
import com.oracle.javafx.scenebuilder.fs.preference.global.InitialDirectoryPreference;
import com.oracle.javafx.scenebuilder.fs.util.FileWatcher;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
public class FileSystemController implements FileWatcher.Delegate, FileSystem {

	private final static Logger log = Logger.getLogger(FileSystemController.class.getName());

	private final InitialDirectoryPreference initialDirectoryPreference;

	private final Map<Object, List<Path>> watchedFiles = new HashMap<>();
	private final Map<Path, WatchingCallback> watchCallbacks = new HashMap<>();

	private final FileWatcher fileWatcher
		= new FileWatcher(2000 /* ms */, this, FileSystemController.class.getSimpleName());

    private File messageBoxFolder;
    private File userLibraryFolder;
    private File applicationDataFolder;


	public FileSystemController(
			@Autowired InitialDirectoryPreference initialDirectoryPreference
			) {
		this.initialDirectoryPreference = initialDirectoryPreference;
	}

	@Override
    public synchronized File getApplicationDataFolder() {

        if (applicationDataFolder == null) {
            final String appName = APP_FOLDER_NAME; //NOI18N

            if (EditorPlatform.IS_WINDOWS) {
                applicationDataFolder
                        = new File(System.getenv("APPDATA") + "\\" + appName); //NOI18N
            } else if (EditorPlatform.IS_MAC) {
                applicationDataFolder
                        = new File(System.getProperty("user.home") //NOI18N
                        + "/Library/Application Support/" //NOI18N
                        + appName);
            } else if (EditorPlatform.IS_LINUX) {
                applicationDataFolder
                        = new File(System.getProperty("user.home") + "/.scenebuilder"); //NOI18N
            }
        }

        assert applicationDataFolder != null;

        return applicationDataFolder;
    }


    @Override
    public synchronized File getUserLibraryFolder() {

        if (userLibraryFolder == null) {
            userLibraryFolder = new File(getApplicationDataFolder(), "/Library"); //NOI18N
        }

        return userLibraryFolder;
    }

    @Override
    public File getMessageBoxFolder() {

        if (messageBoxFolder == null) {
            messageBoxFolder = new File(getApplicationDataFolder(), "/MB"); //NOI18N
        }

        return messageBoxFolder;
    }

    @Override
    public File getNextInitialDirectory() {
        return initialDirectoryPreference.getValue();
    }

    @Override
    public void updateNextInitialDirectory(File chosenFile) {
        assert chosenFile != null;

        final Path chosenFolder = chosenFile.toPath().getParent();
        if (chosenFolder != null) {
        	initialDirectoryPreference.setValue(chosenFolder.toFile()).writeToJavaPreferences();
        }
    }



    @Override
	public void watch(Object key, Set<Path> files, WatchingCallback callback) {
    	List<File> fileList = files.stream().map(p -> p.toFile()).collect(Collectors.toList());
    	watch(key, fileList, callback);
	}

	@Override
	public void watch(Object key, List<File> files, WatchingCallback callback) {
		if (files != null && !files.isEmpty()) {
			List<Path> paths = files.stream()
				.filter(f -> f != null && f.exists())
				.map(f -> f.toPath())
				.collect(Collectors.toList());

		watchedFiles.put(key, paths);

			paths.forEach(p -> {
				fileWatcher.addTarget(p);
				watchCallbacks.put(p, callback);
				log.log(Level.INFO, "Watching file : {0}", p.toAbsolutePath());
			});
		}
	}

	@Override
	public void unwatch(Object key) {
		if (watchedFiles.containsKey(key)) {
			watchedFiles.get(key).forEach(p -> watchCallbacks.remove(p));
			watchedFiles.remove(key);
		}
	}

	@Override
	public void startWatcher() {
		log.log(Level.INFO, "Starting filewatcher !");
        fileWatcher.start();
    }

	@Override
    public void stopWatcher() {
		log.log(Level.INFO, "Stoping filewatcher !");
        fileWatcher.stop();
    }

    /*
     * FileWatcher.Delegate
     */
	@Override
	public void fileWatcherDidWatchTargetCreation(Path target) {
		log.log(Level.INFO, "File Event : file created ({0})", target.toFile().getName());
		if (watchCallbacks.containsKey(target)) {
			log.log(Level.INFO, "File Event sent : file created ({0})", target.toFile().getName());
			watchCallbacks.get(target).created(target);
		}
	}

	@Override
	public void fileWatcherDidWatchTargetDeletion(Path target) {
		log.log(Level.INFO, "File Event : file deleted ({0})", target.toFile().getName());
		if (watchCallbacks.containsKey(target)) {
			log.log(Level.INFO, "File Event sent : file deleted ({0})", target.toFile().getName());
			watchCallbacks.get(target).deleted(target);
		}
	}

	@Override
	public void fileWatcherDidWatchTargetModification(Path target) {
		log.log(Level.INFO, "File Event : file modified ({0})", target.toFile().getName());
		if (watchCallbacks.containsKey(target)) {
			log.log(Level.INFO, "File Event sent : file modified ({0})", target.toFile().getName());
			watchCallbacks.get(target).modified(target);
		}
	}


}
