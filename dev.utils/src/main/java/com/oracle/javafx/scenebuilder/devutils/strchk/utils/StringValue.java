package com.oracle.javafx.scenebuilder.devutils.strchk.utils;

import com.oracle.javafx.scenebuilder.devutils.strchk.Config;

public class StringValue {

    
    public static boolean isValidCandidate(String value) {
        
        if (value == null) {
            return false;
        }
        
        if (value.isEmpty()) {
            return false;
        }
        
        String  mvalue = value.trim();
        mvalue = mvalue.replace("\\n", "");
        mvalue = mvalue.replace("\\t", "");
        
        if (mvalue.length() <= 1) {
            return false;
        }
        
        try {
            Double.parseDouble(mvalue);
            return false;
        } catch (NumberFormatException e) {}
        
        if (!Config.DISABLE_ALL_FILTERS) {
            if (Config.EXCLUDED_VALUES.contains(mvalue)) {
                return false;
            }
            
            for (String s:Config.EXCLUDE_VALUES_STARTING_WITH) {
                if (mvalue.startsWith(s)) {
                    return false;
                }
            }
            
            for (String s:Config.EXCLUDE_VALUES_CONTAINING) {
                if (mvalue.contains(s)) {
                    return false;
                }
            }
    
            boolean patternFound = Config.EXCLUDE_VALUES_WITH_PATTERN.stream().anyMatch(p -> p.matcher(value).matches());
            if (patternFound) {
                return false;
            }
        }
        
        return true;
    }
}
