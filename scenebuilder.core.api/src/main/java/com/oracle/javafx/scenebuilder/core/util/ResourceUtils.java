/*
 * Copyright (c) 2016, 2021, Gluon and/or its affiliates.
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

package com.oracle.javafx.scenebuilder.core.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResourceUtils {

    private static List<String> imageExtensions;
    private static List<String> audioExtensions;
    private static List<String> videoExtensions;
    private static List<String> mediaExtensions;

    public static List<String> getSupportedImageExtensions() {
        if (imageExtensions == null) {
            imageExtensions = new ArrayList<>();
            imageExtensions.add("*.jpg"); //NOCHECK
            imageExtensions.add("*.jpeg"); //NOCHECK
            imageExtensions.add("*.png"); //NOCHECK
            imageExtensions.add("*.gif"); //NOCHECK
            imageExtensions = Collections.unmodifiableList(imageExtensions);
        }
        return imageExtensions;
    }

    public static List<String> getSupportedAudioExtensions() {
        if (audioExtensions == null) {
            audioExtensions = new ArrayList<>();
            audioExtensions.add("*.aif"); //NOCHECK
            audioExtensions.add("*.aiff"); //NOCHECK
            audioExtensions.add("*.mp3"); //NOCHECK
            audioExtensions.add("*.m4a"); //NOCHECK
            audioExtensions.add("*.wav"); //NOCHECK
            audioExtensions.add("*.m3u"); //NOCHECK
            audioExtensions.add("*.m3u8"); //NOCHECK
            audioExtensions = Collections.unmodifiableList(audioExtensions);
        }
        return audioExtensions;
    }

    public static List<String> getSupportedVideoExtensions() {
        if (videoExtensions == null) {
            videoExtensions = new ArrayList<>();
            videoExtensions.add("*.flv"); //NOCHECK
            videoExtensions.add("*.fxm"); //NOCHECK
            videoExtensions.add("*.mp4"); //NOCHECK
            videoExtensions.add("*.m4v"); //NOCHECK
            videoExtensions = Collections.unmodifiableList(videoExtensions);
        }
        return videoExtensions;
    }

    public static List<String> getSupportedMediaExtensions() {
        if (mediaExtensions == null) {
            mediaExtensions = new ArrayList<>();
            mediaExtensions.addAll(getSupportedImageExtensions());
            mediaExtensions.addAll(getSupportedAudioExtensions());
            mediaExtensions.addAll(getSupportedVideoExtensions());
            mediaExtensions = Collections.unmodifiableList(mediaExtensions);
        }
        return mediaExtensions;
    }
}
