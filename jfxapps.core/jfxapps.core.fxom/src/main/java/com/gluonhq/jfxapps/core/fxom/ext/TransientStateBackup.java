package com.gluonhq.jfxapps.core.fxom.ext;

import com.gluonhq.jfxapps.core.fxom.FXOMObject;

public interface TransientStateBackup {

    boolean canHandle(FXOMObject candidate);
    void backup(FXOMObject candidate);
    void restore(FXOMObject candidate); 
    
}
