package com.oracle.javafx.scenebuilder.devutils.strchk.utils;

import java.util.regex.Pattern;

public interface Patterns {

    public static Pattern STRING = Pattern.compile("\"(.*?)\"");
    
    public static Pattern STRING_IN_FXML = Pattern.compile("(?:source|url|stylesheets|resources)=\"(.*?)\"");
    public static Pattern I18N_STRING_IN_FXML = Pattern.compile("=\"%(.*?)\"");
    
    public static Pattern PACKAGE = Pattern.compile("package (.*);");
    public static Pattern COMPONENT = Pattern.compile("@Component");
    public static Pattern INNER_COMPONENT = Pattern.compile("\\{(.*)@Component(.*)}");
    
}
