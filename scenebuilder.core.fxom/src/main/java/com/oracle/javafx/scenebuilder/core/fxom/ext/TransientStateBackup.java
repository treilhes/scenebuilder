package com.oracle.javafx.scenebuilder.core.fxom.ext;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;

public interface TransientStateBackup {

    boolean canHandle(FXOMObject candidate);
    void backup(FXOMObject candidate);
    void restore(FXOMObject candidate); 
    
}
