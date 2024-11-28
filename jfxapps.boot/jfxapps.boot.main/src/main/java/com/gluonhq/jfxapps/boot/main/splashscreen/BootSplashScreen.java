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
package com.gluonhq.jfxapps.boot.main.splashscreen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.SplashScreen;

public class BootSplashScreen {

    private static BootSplashScreen instance;
    private Thread progressThread;
    private boolean closeRequested;
    private SplashScreen splash;

    public static BootSplashScreen getInstance() {
        if (instance == null) {
            instance = new BootSplashScreen();
        }
        return instance;
    }

    private BootSplashScreen() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }

        splash = SplashScreen.getSplashScreen();
    }

    public void start() {


        if (splash == null) {
            System.out.println("No splash screen available");
            return;
        }

        progressThread = new Thread(() -> {
            simulateProgress(splash);
        });

        progressThread.start();
    }

    public void stop() {
        closeRequested = true;
    }

    private void simulateProgress(SplashScreen splash) {
        Graphics2D g = splash.createGraphics();
        if (g == null) {
            System.out.println("Graphics not available");
            return;
        }

        // Simulate loading task with progress
        for (int i = 0; i <= 100; i++) {

            if (closeRequested) {
                break;
            }

            g.setColor(Color.BLACK);
            g.fillRect(0, splash.getSize().height - 20, splash.getSize().width, 20);
            g.setColor(Color.GREEN);
            g.fillRect(0, splash.getSize().height - 20, i * splash.getSize().width / 100, 20);
            g.setColor(Color.BLACK);
            g.drawString(i + "%", splash.getSize().width / 2, splash.getSize().height - 5);
            splash.update();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        splash.close();
    }
}
