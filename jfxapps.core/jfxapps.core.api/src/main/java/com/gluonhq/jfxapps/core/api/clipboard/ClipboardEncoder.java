package com.gluonhq.jfxapps.core.api.clipboard;

import java.util.List;

import com.gluonhq.jfxapps.core.fxom.FXOMObject;

import javafx.scene.input.ClipboardContent;

public interface ClipboardEncoder {

    boolean isEncodable(List<? extends FXOMObject> fxomObjects);

    ClipboardContent makeEncoding(List<? extends FXOMObject> fxomObjects);

}