package com.oracle.javafx.scenebuilder.app.settings;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.function.Consumer;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;

import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.app.about.AboutWindowController;

@Component
public class VersionSetting {

	public static final String LATEST_VERSION_CHECK_URL = "http://download.gluonhq.com/scenebuilder/settings.properties";
    public static final String LATEST_VERSION_NUMBER_PROPERTY = "latestversion";

    public static final String LATEST_VERSION_INFORMATION_URL = "http://download.gluonhq.com/scenebuilder/version-8.4.0.json";

    public static final String DOWNLOAD_URL = "http://gluonhq.com/labs/scene-builder";

    private String sceneBuilderVersion;
    private String latestVersion;

    private String latestVersionText;
    private String latestVersionAnnouncementURL;

    private JsonReaderFactory readerFactory = Json.createReaderFactory(null);

    public VersionSetting() {
    	initSceneBuiderVersion();
	}

    private void initSceneBuiderVersion() {
        try (InputStream in = AboutWindowController.class.getResourceAsStream("about.properties")) {
            if (in != null) {
                Properties sbProps = new Properties();
                sbProps.load(in);
                sceneBuilderVersion = sbProps.getProperty("build.version", "UNSET");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    

    public String getSceneBuilderVersion() {
        return sceneBuilderVersion;
    }

    public boolean isCurrentVersionLowerThan(String version) {
        String[] versionNumbers = version.split("\\.");
        String[] currentVersionNumbers = sceneBuilderVersion.split("\\.");
        for (int i = 0; i < versionNumbers.length; ++i) {
            int number = Integer.parseInt(versionNumbers[i]);
            int currentVersionNumber = Integer.parseInt(currentVersionNumbers[i]);
            if (number > currentVersionNumber) {
                return true;
            } else if (number < currentVersionNumber) {
                return false;
            }
        }
        return false;
    }

    public void getLatestVersion(Consumer<String> consumer) {

        if (latestVersion == null) {
            new Thread (() -> {
                Properties prop = new Properties();
                String onlineVersionNumber = null;

                URL url = null;
                try {
                    url = new URL(LATEST_VERSION_CHECK_URL);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                try (InputStream inputStream = url.openStream()) {
                    prop.load(inputStream);
                    onlineVersionNumber = prop.getProperty(LATEST_VERSION_NUMBER_PROPERTY);

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                latestVersion = onlineVersionNumber;
                consumer.accept(latestVersion);
            }, "GetLatestVersion").start();
        } else {
            consumer.accept(latestVersion);
        }
    }

    public String getLatestVersionText() {
        if (latestVersionText == null) {
            updateLatestVersionInfo();
        }
        return latestVersionText;
    }

    private void updateLatestVersionInfo() {
        try {
            URL url = new URL(LATEST_VERSION_INFORMATION_URL);

            try (JsonReader reader = readerFactory.createReader(new InputStreamReader(url.openStream()))) {
                JsonObject object = reader.readObject();
                JsonObject announcementObject = object.getJsonObject("announcement");
                latestVersionText = announcementObject.getString("text");
                latestVersionAnnouncementURL = announcementObject.getString("url");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public String getLatestVersionAnnouncementURL() {
        if (latestVersionAnnouncementURL == null) {
            updateLatestVersionInfo();
        }
        return latestVersionAnnouncementURL;
    }

}
