package com.oracle.javafx.scenebuilder.api;

import com.oracle.javafx.scenebuilder.api.i18n.I18N;

/**
 * Predefined sizes (width x height).
 * Preferred one refers to the one explicitly set by the user: it is for
 * use for previewing only.
 * Default one is the one stored as a global preference: its value is
 * under user control.
 */
public enum Size {
    SIZE_335x600(335,600,"menu.title.size.phone"),
    SIZE_900x600(900,600,"menu.title.size.tablet"),
    SIZE_320x240(320,240,"menu.title.size.qvga"),
    SIZE_640x480(640,480,"menu.title.size.vga"),
    SIZE_1280x800(1280,800,"menu.title.size.touch"),
    SIZE_1920x1080(1920,1080,"menu.title.size.hd"),
    SIZE_PREFERRED(-1,-1,"menu.title.size.preferred"),
    SIZE_DEFAULT(-1,-1,null);

    private final int width;
    private final int height;
    private final String nameKey;

    Size(int width, int height, String nameKey) {
        this.width = width;
        this.height = height;
        this.nameKey = nameKey;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public String toString() {
        if (nameKey == null) {
            return "DEFAULT";
        }
        return I18N.getString(nameKey);
    }
}