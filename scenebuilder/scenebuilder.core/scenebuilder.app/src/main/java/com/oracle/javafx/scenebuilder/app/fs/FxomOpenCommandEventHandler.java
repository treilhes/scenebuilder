package com.oracle.javafx.scenebuilder.app.fs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.core.api.application.ApplicationActionFactory;
import com.gluonhq.jfxapps.core.api.application.OpenCommandEventHandler;
import com.gluonhq.jfxapps.core.api.document.DocumentActionFactory;
import com.gluonhq.jfxapps.core.api.fs.OpenFileHandler;
import com.gluonhq.jfxapps.core.api.ui.dialog.ApplicationDialog;
import com.treilhes.emc4j.boot.api.context.annotation.ApplicationSingleton;
import com.treilhes.emc4j.boot.api.loader.OpenCommandEvent;

@ApplicationSingleton
public class FxomOpenCommandEventHandler implements OpenCommandEventHandler {

    private static final String FILE_ARG = "--file";

    private static final Logger logger = LoggerFactory.getLogger(FxomOpenCommandEventHandler.class);

    private final ApplicationActionFactory applicationActionFactory;
    private final DocumentActionFactory documentActionFactory;
    private final ApplicationDialog applicationDialog;
    private final List<OpenFileHandler> openFileHandlers;

    public FxomOpenCommandEventHandler(
            ApplicationActionFactory applicationActionFactory,
            DocumentActionFactory documentActionFactory, 
            ApplicationDialog applicationDialog,
            List<OpenFileHandler> openFileHandlers) {
        this.applicationActionFactory = applicationActionFactory;
        this.documentActionFactory = documentActionFactory;
        this.applicationDialog = applicationDialog;
        this.openFileHandlers = openFileHandlers;
    }

    @Override
    public void handleOpenCommand(OpenCommandEvent command) {

        var files = new ArrayList<File>();
        var args = command.getArguments();

        if (args != null && args.contains(FILE_ARG)) {
            int index = args.indexOf(FILE_ARG);
            var filePathes = args.size() > index + 1 ? args.get(index + 1) : null;

            if (filePathes != null) {
                var pathes = filePathes.split(";");
                for (var path : pathes) {
                    files.add(new File(path));
                }
            }

            for (var file : files) {
                try {
                    if (file != null) {
                        for (var handler : openFileHandlers) {
                            if (handler.canOpen(file)) {
                                handler.open(file);
                            }
                        }
                    } else {
                        applicationActionFactory.lookupUnusedInstance(null, (instance) -> {
                            instance.openWindow();
                            documentActionFactory.loadBlank().perform();
                        }).perform();
                    }
                } catch (Exception e) {
                    logger.error("Error while executing command", e);
                    applicationDialog.addError("Error while executing command", e.getMessage(), e);
                }
            }

        } else {
            applicationActionFactory.lookupUnusedInstance(null, (instance) -> {
                instance.openWindow();
                documentActionFactory.loadBlank().perform();
            }).perform();
        }

    }
}
