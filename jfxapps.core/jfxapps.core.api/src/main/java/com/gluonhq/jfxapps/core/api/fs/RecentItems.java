package com.gluonhq.jfxapps.core.api.fs;

import java.io.File;
import java.net.URL;
import java.util.List;

import javafx.collections.ObservableList;

public interface RecentItems {

    boolean containsRecentItem(File file);

    boolean containsRecentItem(URL url);

    void addRecentItem(File file);

    void addRecentItem(URL url);

    void addRecentItems(List<File> files);

    void removeRecentItems(List<String> filePaths);

    void clearRecentItems();

    ObservableList<String> getRecentItems();

}