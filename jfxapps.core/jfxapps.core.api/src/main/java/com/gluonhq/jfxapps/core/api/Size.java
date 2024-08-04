/*
 * Copyright (c) 2016, 2024, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2024, Pascal Treilhes and/or its affiliates.
 * Copyright (c) 2012, 2014, Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation and Gluon nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.gluonhq.jfxapps.core.api;

import com.gluonhq.jfxapps.core.api.i18n.I18N;

/**
 * Predefined sizes (width x height).
 * Preferred one refers to the one explicitly set by the user: it is for
 * use for previewing only.
 * Default one is the one stored as a global preference: its value is
 * under user control.
 */
public enum Size {
    SIZE_335x600(335,600,"c"),
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

    public String getI18nKey() {
        return nameKey;
    }

    @Override
    public String toString() {
        if (nameKey == null) {
            return "DEFAULT";
        }
        //FIXME before the key was resolved but not anymore
        // need to find where the toString method was used and use the i18n bean to resolve it
        //return I18N.getString(nameKey);
        return nameKey;
    }
}