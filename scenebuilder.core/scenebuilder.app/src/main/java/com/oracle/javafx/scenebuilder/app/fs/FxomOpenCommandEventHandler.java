package com.oracle.javafx.scenebuilder.app.fs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.treilhes.emc4j.boot.api.context.annotation.ApplicationSingleton;
import com.treilhes.emc4j.boot.api.loader.OpenCommandEvent;
import com.treilhes.emc4j.boot.api.loader.RestartCommandEvent;
import com.treilhes.emc4j.boot.api.loader.RestartedCommandEvent;
import com.treilhes.emc4j.boot.api.loader.StopCommandEvent;
import com.treilhes.jfxplace.core.api.application.ApplicationActionFactory;
import com.treilhes.jfxplace.core.api.application.CommandEventHandler;
import com.treilhes.jfxplace.core.api.fs.OpenFileHandler;
import com.treilhes.jfxplace.core.api.ui.dialog.ApplicationDialog;
import com.treilhes.jfxplace.fxom.api.document.DocumentActionFactory;

@ApplicationSingleton
public class FxomOpenCommandEventHandler implements CommandEventHandler {

    private static final String FILE_ARG = "--file";

    private static final Logger logger = LoggerFactory.getLogger(FxomOpenCommandEventHandler.class);

    private final ApplicationActionFactory applicationActionFactory;
    private final ApplicationDialog applicationDialog;
    private final List<OpenFileHandler> openFileHandlers;

    public FxomOpenCommandEventHandler(
            ApplicationActionFactory applicationActionFactory,
            ApplicationDialog applicationDialog,
            List<OpenFileHandler> openFileHandlers) {
        this.applicationActionFactory = applicationActionFactory;
        this.applicationDialog = applicationDialog;
        this.openFileHandlers = openFileHandlers;
    }

    @Override
    public void handleOpenCommand(OpenCommandEvent command) {

        var args = command != null ? command.getArguments() : null;

        if (args != null && args.contains(FILE_ARG)) {
            var files = new ArrayList<File>();

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
                        applicationActionFactory.lookupUnusedInstance(null, instance -> {
                            var ctx = instance.getContext();
                            var ui = instance.getUi();
                            var documentActionFactory = ctx.getBean(DocumentActionFactory.class);
                            ui.openWindow();
                            documentActionFactory.loadBlank().perform();
                        }).perform();
                    }
                } catch (Exception e) {
                    logger.error("Error while executing command", e);
                    applicationDialog.addError("Error while executing command", e.getMessage(), e);
                }
            }

        } else {
            applicationActionFactory.lookupUnusedInstance(null, instance -> {
                var ctx = instance.getContext();
                var ui = instance.getUi();
                var documentActionFactory = ctx.getBean(DocumentActionFactory.class);
                ui.openWindow();
                documentActionFactory.loadBlank().perform();
            }).perform();
        }

    }

    @Override
    public void handleRestartCommand(RestartCommandEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handleStopCommand(StopCommandEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handleRestartedCommand(RestartedCommandEvent command) {
        handleOpenCommand(null);
    }
}
