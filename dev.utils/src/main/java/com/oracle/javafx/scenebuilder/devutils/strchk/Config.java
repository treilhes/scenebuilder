package com.oracle.javafx.scenebuilder.devutils.strchk;

import java.util.List;
import java.util.regex.Pattern;

public class Config {
    
    public static final String ROOT_PROJECT = "../";
    
    public static final String PROJECT_JAVA_FOLDER = "src/main/java";
    public static final String PROJECT_RESOURCE_FOLDER = "src/main/resources";
    
    
    public static final List<String> EXCLUDED_PROJECTS = List.of("dev.utils", "docs");
    
    public static boolean DISABLE_ALL_FILTERS = false;
    
    public static final List<String> EXCLUDE_LINES_WITH_PREFIX = List.of();
    public static final List<String> EXCLUDE_LINES_WITH_SUFFIX = List.of(
            "//NOCHECK", "//NOCHECK", "// NOI18N", "// NOCHECK");
    
    public static final List<Pattern> EXCLUDE_LINES_WITH_PATTERN = List.of(
                Pattern.compile(".*@SuppressWarnings\\(\".*?\"\\).*"),
                Pattern.compile(".*Exception\\(\".*?\"\\);.*"),
                Pattern.compile(".*logger\\..*?\\(\".*?\".*"),
                Pattern.compile(".*\\*.*")
            );
    
    public static final List<Pattern> INCLUDE_LINES_WITH_PATTERN = List.of(
            Pattern.compile(".*\\*.*src=\"doc-files/.*")
        );
    public static final List<String> EXCLUDED_VALUES = List.of("AS IS", "UNSET", "true", "false", "null", "\\n", "\\n\\n");
    public static final List<String> EXCLUDE_VALUES_STARTING_WITH = List.of("http://", "https://","-");
    public static final List<String> EXCLUDE_VALUES_CONTAINING = List.of("*"," ");
    public static final List<Pattern> EXCLUDE_VALUES_WITH_PATTERN = List.of(
            Pattern.compile("fx:.*"),
            Pattern.compile("[a-z0-9]{8}-[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{12}")
            );
    
}
