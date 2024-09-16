/*
 * Copyright (c) 2016, 2023, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2023, Pascal Treilhes and/or its affiliates.
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
package com.gluonhq.jfxapps.boot.platform.internal;

import java.io.File;
import java.util.UUID;

import com.gluonhq.jfxapps.boot.api.platform.JfxAppsPlatform;

public class DefaultFolders {

    public static String APP_FOLDER_NAME = "JfxApps";

    private static File messageBoxFolder;

    private static File applicationDataFolder;


	private DefaultFolders() {}

    public static synchronized File getApplicationDataFolder() {

        if (applicationDataFolder == null) {
            final String appName = APP_FOLDER_NAME; //NOCHECK

            if (JfxAppsPlatform.IS_WINDOWS) {
                applicationDataFolder
                        = new File(System.getenv("APPDATA") + "\\" + appName); //NOCHECK
            } else if (JfxAppsPlatform.IS_MAC) {
                applicationDataFolder
                        = new File(System.getProperty("user.home") //NOCHECK
                        + "/Library/Application Support/" //NOCHECK
                        + appName);
            } else if (JfxAppsPlatform.IS_LINUX) {
                applicationDataFolder
                        = new File(System.getProperty("user.home") + "/.scenebuilder"); //NOCHECK
            }
        }

        assert applicationDataFolder != null;

        return applicationDataFolder;
    }

    public static synchronized File getUserExtensionsFolder(UUID extensionId) {
        return new File(getApplicationDataFolder(), extensionId.toString()); //NOCHECK
    }

    public static synchronized File getMessageBoxFolder() {

        if (messageBoxFolder == null) {
            messageBoxFolder = new File(getApplicationDataFolder(), "/MB"); //NOCHECK
        }

        return messageBoxFolder;
    }

}
