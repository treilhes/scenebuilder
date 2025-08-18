package com.gluonhq.jfxapps.app.devtools.modelv2;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

import com.gluonhq.jfxapps.core.api.fs.watcher.File;
import com.gluonhq.jfxapps.core.api.fs.watcher.FileType;
import com.gluonhq.jfxapps.core.api.fs.watcher.Folder;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

public class PropertiesFile extends File {

    private ObservableMap<String, String> properties = FXCollections.observableHashMap();

    public PropertiesFile(Folder parent, Path location, FileType fileType) {
        super(parent, location, fileType);
    }


    @Override
    public void refresh() {
        try {
            Properties p = new Properties();
            p.load(getPath().toUri().toURL().openStream());

            p.entrySet().forEach(entry -> {
                String key = entry.getKey().toString();
                String value = entry.getValue() != null ? entry.getValue().toString() : null;
                properties.put(key, value);
            });

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    @Override
    public String toString() {
        return "PropertiesFile [getLocation()=" + getPath() + "]";
    }

}