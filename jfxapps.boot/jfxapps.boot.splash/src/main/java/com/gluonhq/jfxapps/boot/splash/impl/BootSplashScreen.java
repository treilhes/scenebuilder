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
package com.gluonhq.jfxapps.boot.splash.impl;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.SplashScreen;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JWindow;

public class BootSplashScreen {

	private static final int PROGRESS_BAR_HEIGHT = 4;
	private static final Color PROGRESSBAR_BACKGROUND_COLOR = Color.BLACK;
	private static final Color PROGRESSBAR_COLOR = Color.GREEN;

	private static BootSplashScreen instance;

	private final BootLoadingProgress root;
	private final SplashScreen splash;
	private FallBackSplashScreen fallbackSplash;

	private Thread progressThread;
	private boolean closeRequested;

	public static BootSplashScreen getInstance(BootLoadingProgress loadingProgress) {
		if (instance == null) {
			instance = new BootSplashScreen(loadingProgress);
		}
		return instance;
	}

	private BootSplashScreen(BootLoadingProgress loadingProgress) {
		if (GraphicsEnvironment.isHeadless()) {
			this.root = null;
			this.splash = null;
			return;
		}
		this.root = loadingProgress;
		this.splash = SplashScreen.getSplashScreen();
		start();
	}

	public void start() {
		if (progressThread != null && progressThread.isAlive()) {
			return;
		}

		final Graphics2D g;
		final int width;
		final int height;
		if (splash == null || !splash.isVisible()) {
			fallbackSplash = new FallBackSplashScreen(root.getImageUrl());
			g = fallbackSplash.getGraphics();
			width = fallbackSplash.getWidth();
			height = fallbackSplash.getHeight();
		} else {
			g = splash.createGraphics();
			width = splash.getSize().width;
			height = splash.getSize().height;
		}


		progressThread = new Thread(() -> {

			while (!root.isDone() && !closeRequested) {
				updateProgress(g, width, height);

				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			if (fallbackSplash != null) {
				fallbackSplash.setVisible(false);
				fallbackSplash.dispose();
			}
			if (splash != null && splash.isVisible()) {
				splash.close();
			}
		});

		progressThread.setName("Splash-Thread");
		progressThread.start();

	}

	private void update() {
		if (fallbackSplash != null) {

		} else if (splash != null && splash.isVisible()) {
			splash.update();
		}
	}

	public void stop() {
		closeRequested = true;
	}

	private void updateProgress(Graphics2D g, int width, int height) {

		if (g == null) {
			System.out.println("Graphics not available");
			return;
		}

		if (root == null) {
			System.out.println("no root splash");
			return;
		}

		int progress = (int)(width * root.getCurrentProgress());

		g.setColor(PROGRESSBAR_BACKGROUND_COLOR);
        g.fillRect(0, height - PROGRESS_BAR_HEIGHT, width, PROGRESS_BAR_HEIGHT);
        g.setColor(PROGRESSBAR_COLOR);
        g.fillRect(0, height - PROGRESS_BAR_HEIGHT, progress, PROGRESS_BAR_HEIGHT);
        //g.setColor(PROGRESSBAR_BACKGROUND_COLOR);
        //g.drawString(i + "%", splash.getSize().width / 2, splash.getSize().height - 5);

        update();
	}

	private class FallBackSplashScreen extends JWindow {

		private static final URL DEFAULT_IMAGE_URL = FallBackSplashScreen.class.getResource("splash.png");

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		private BufferedImage image;

		public FallBackSplashScreen(URL url) {
			if (url == null) {
				url = DEFAULT_IMAGE_URL;
			}

			image = loadImage(url);

			if (image == null) {
				image = loadImage(DEFAULT_IMAGE_URL);
			}

			setSize(image.getWidth(), image.getHeight());
			getGraphics().drawImage(image, 0, 0, null);
		}

		@Override
		public void setSize(int width, int height) {
			// Set the size of the splash screen
			super.setSize(width, height);

			// Center the splash screen on the screen
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			int x = (screenSize.width - getWidth()) / 2;
			int y = (screenSize.height - getHeight()) / 2;
			setLocation(x, y);
		}

		public Graphics2D getGraphics() {
			return (Graphics2D) super.getGraphics();
		}

		private BufferedImage loadImage(URL imageUrl) {
			try {
				return ImageIO.read(imageUrl);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
	}
}
