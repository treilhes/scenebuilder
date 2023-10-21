package com.oracle.javafx.scenebuilder.maven.client.api;

public final class MavenClassifier {

    public static final String POM_EXTENSION = "pom";
    public static final String JAR_EXTENSION = "jar";
    public static final String SHA1_EXTENSION = "sha1";

    public static final MavenClassifier DEFAULT = new MavenClassifier("", JAR_EXTENSION);
    public static final MavenClassifier DEFAULT_SHA1 = new MavenClassifier("", JAR_EXTENSION + "." + SHA1_EXTENSION);
    public static final MavenClassifier SOURCES = new MavenClassifier("sources", JAR_EXTENSION);
    public static final MavenClassifier SOURCES_SHA1 = new MavenClassifier("sources", JAR_EXTENSION + "." + SHA1_EXTENSION);
    public static final MavenClassifier JAVADOC = new MavenClassifier("javadoc", JAR_EXTENSION);
    public static final MavenClassifier JAVADOC_SHA1 = new MavenClassifier("javadoc", JAR_EXTENSION + "." + SHA1_EXTENSION);
    public static final MavenClassifier POM = new MavenClassifier("", POM_EXTENSION);
    public static final MavenClassifier POM_SHA1 = new MavenClassifier("", POM_EXTENSION + "." + SHA1_EXTENSION);

    private String classifier;
    private String extension;

    public MavenClassifier(String classifier, String extension) {
        super();
        this.classifier = classifier;
        this.extension = extension;
    }

    public String getClassifier() {
        return classifier;
    }

    public String getExtension() {
        return extension;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((classifier == null) ? 0 : classifier.hashCode());
        result = prime * result + ((extension == null) ? 0 : extension.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MavenClassifier other = (MavenClassifier) obj;
        if (classifier == null) {
            if (other.classifier != null)
                return false;
        } else if (!classifier.equals(other.classifier))
            return false;
        if (extension == null) {
            if (other.extension != null)
                return false;
        } else if (!extension.equals(other.extension))
            return false;
        return true;
    }

}
