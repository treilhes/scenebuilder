/*
 * Copyright (c) 2016, 2025, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2025, Pascal Treilhes and/or its affiliates.
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
package com.gluonhq.jfxapps.boot.maven.client.prompt;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.AbstractBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CredentialPrompt {

    private static final Logger LOGGER = LoggerFactory.getLogger(CredentialPrompt.class);
    private static final Color BORDER_COLOR = Color.GRAY;

    private CredentialPrompt() {
        throw new IllegalStateException("Utility class");
    }

    public static Credentials requestCredentialsFor(URL target) {
        try {
            RoundedLoginUI loginUI = new RoundedLoginUI(target.getHost(), "", "");
            var future = loginUI.show();
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("Error obtaining credentials", e);
            return null;
        }
    }

    public static RoundedLoginUI promptFor(URL target) {
        return new RoundedLoginUI(target.getHost(), "", "");
    }

    public static boolean validateBasicAuth(URL url, String username, String password) {
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Build "Authorization: Basic base64(user:pass)" header
            String auth = username + ":" + password;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            conn.setRequestProperty("Authorization", "Basic " + encodedAuth);

            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int responseCode = conn.getResponseCode();

            // 200 = OK (authenticated), 401 = Unauthorized
            return responseCode == 200;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static class Credentials {
        private final String username;
        private final String password;

        public Credentials(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }

    static class RoundedLoginUI {

        private CompletableFuture<Credentials> future = new CompletableFuture<>();
        private String username;
        private String password;
        private String url;
        private JTextField usernameField;
        private CustomPasswordField passwordField;
        private RoundedButton loginBtn;
        private RoundedButton cancelBtn;
        private JFrame frame;

        public RoundedLoginUI(String url, String username, String password) {
            super();
            this.url = url;
            this.username = username;
            this.password = password;
        }

        public Future<Credentials> show() {
            frame = new JFrame("Login");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(350, 550);
            frame.setLocationRelativeTo(null);
            frame.setUndecorated(true); // Required for custom rounded borders
            frame.setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f)); // Transparent background

            // Inner panel for layout and components
            RoundedPanel panel = new RoundedPanel(20, new Color(245, 247, 255), BORDER_COLOR);
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
            panel.setOpaque(false); // Important so outer rounded shape shows
            panel.setLocation(1, 1);
            // Load icons
            ImageIcon lockIcon = loadIcon("/icons/lock_icon.png");
            ImageIcon eyeClosedIcon = loadIcon("/icons/eye_closed_icon.png");
            ImageIcon eyeOpenIcon = loadIcon("/icons/eye_open_icon.png");

            // Lock icon
            JLabel lockLabel = new JLabel(lockIcon);
            lockLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(lockLabel);
            panel.add(Box.createRigidArea(new Dimension(0, 20)));

            // Welcome label
            JLabel welcome = new JLabel("Welcome!");
            welcome.setFont(new Font("Arial", Font.BOLD, 16));
            welcome.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(welcome);
            panel.add(Box.createRigidArea(new Dimension(0, 20)));

            JLabel repolabel = new JLabel("Please provide credentials for:");
            repolabel.setFont(new Font("Arial", Font.PLAIN, 16));
            repolabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(repolabel);
            panel.add(Box.createRigidArea(new Dimension(0, 20)));

            // Target URL label
            JLabel target = new JLabel(url);
            target.setFont(new Font("Arial", Font.BOLD, 16));
            target.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(target);
            panel.add(Box.createRigidArea(new Dimension(0, 20)));

            JLabel username = new JLabel("       Username:");
            username.setFont(new Font("Arial", Font.PLAIN, 12));
            username.setAlignmentX(Component.RIGHT_ALIGNMENT);
            username.setPreferredSize(new Dimension(250, 20));
            username.setMaximumSize(new Dimension(250, 20));
            panel.add(username);

            // Username field
            usernameField = new JTextField();
            usernameField.setBorder(new CompoundBorder(new RoundedBorder(10), new EmptyBorder(0, 10, 0, 10)));
            usernameField.setPreferredSize(new Dimension(250, 35));
            usernameField.setMaximumSize(new Dimension(250, 35));
            usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
            if (username != null) {
                usernameField.setText(this.username);
            }
            panel.add(usernameField);
            panel.add(Box.createRigidArea(new Dimension(0, 15)));

            JLabel password = new JLabel("       Password:");
            password.setFont(new Font("Arial", Font.PLAIN, 12));
            password.setAlignmentX(Component.RIGHT_ALIGNMENT);
            password.setPreferredSize(new Dimension(250, 20));
            password.setMaximumSize(new Dimension(250, 20));
            if (password != null) {
                password.setText(this.password);
            }
            panel.add(password);

            // Password field with eye toggle
            JLayeredPane passwordPane = new JLayeredPane();
            passwordPane.setPreferredSize(new Dimension(250, 35));
            passwordPane.setMaximumSize(new Dimension(250, 35));

            passwordField = new CustomPasswordField();
            passwordField.setBorder(new CompoundBorder(new RoundedBorder(10), new EmptyBorder(0, 10, 0, 30)));
            passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
            passwordField.setBounds(0, 0, 250, 35);

            JLabel eyeLabel = new JLabel(eyeClosedIcon);
            eyeLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            eyeLabel.setBounds(220, 7, 20, 20);

            eyeLabel.addMouseListener(new MouseAdapter() {
                private boolean showing = false;

                @Override
                public void mouseClicked(MouseEvent e) {
                    showing = !showing;
                    passwordField.setShowPassword(showing);
                    eyeLabel.setIcon(showing ? eyeOpenIcon : eyeClosedIcon);
                }
            });

            passwordPane.add(passwordField, Integer.valueOf(0));
            passwordPane.add(eyeLabel, Integer.valueOf(1));
            panel.add(passwordPane);
            panel.add(Box.createRigidArea(new Dimension(0, 25)));

            // Buttons panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
            buttonPanel.setOpaque(false);

            loginBtn = new RoundedButton("Log In", new Color(85, 100, 255), Color.WHITE, 10);
            loginBtn.setPreferredSize(new Dimension(100, 35));
            loginBtn.addActionListener(e -> {
                validate();
            });
            loginBtn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    validate();
                }
            });

            cancelBtn = new RoundedButton("Cancel", Color.WHITE, Color.BLACK, 10);
            cancelBtn.setPreferredSize(new Dimension(100, 35));
            cancelBtn.addActionListener(e -> {
                cancel();
            });
            cancelBtn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    cancel();
                }
            });

            buttonPanel.add(cancelBtn);
            buttonPanel.add(loginBtn);
            panel.add(buttonPanel);

            frame.setContentPane(panel);
            frame.setVisible(true);

            return future;
        }

        private void cancel() {
            future.complete(null);
            frame.dispose();
        }

        private void validate() {
            var creds = new Credentials(usernameField.getText(), new String(passwordField.getPassword()));
            future.complete(creds);
            frame.dispose();
        }

        public JTextField getUsernameField() {
            return usernameField;
        }

        public JPasswordField getPasswordField() {
            return passwordField;
        }

        public JButton getLoginBtn() {
            return loginBtn;
        }

        public JButton getCancelBtn() {
            return cancelBtn;
        }
    }

    // Utility to load icons from classpath
    private static ImageIcon loadIcon(String path) {
        URL url = RoundedLoginUI.class.getResource(path);
        if (url != null) {
            return new ImageIcon(url);
        } else {
            System.err.println("Icon not found: " + path);
            return new ImageIcon(); // fallback
        }
    }

    static class RoundedButton extends JButton {

        private final Color bgColor;
        private final Color fgColor;
        private final int radius;

        public RoundedButton(String text, Color bgColor, Color fgColor, int radius) {
            super(text);
            this.bgColor = bgColor;
            this.fgColor = fgColor;
            this.radius = radius;

            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setForeground(fgColor);
            setFont(new Font("Arial", Font.BOLD, 14));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            // Background
            g2.setColor(isEnabled() ? bgColor : bgColor.darker());
            g2.fillRoundRect(0, 0, width, height, radius, radius);

            // Hover/press overlays
            if (getModel().isRollover()) {
                g2.setColor(new Color(255, 255, 255, 30));
                g2.fillRoundRect(0, 0, width, height, radius, radius);
            } else if (getModel().isPressed()) {
                g2.setColor(new Color(0, 0, 0, 40));
                g2.fillRoundRect(0, 0, width, height, radius, radius);
            }

            // Draw thin black border
            g2.setStroke(new BasicStroke(1f));
            g2.setColor(BORDER_COLOR);
            g2.drawRoundRect(0, 0, width - 1, height - 1, radius, radius);

            // Draw text
            FontMetrics fm = g2.getFontMetrics();
            int textX = (width - fm.stringWidth(getText())) / 2;
            int textY = (height + fm.getAscent() - fm.getDescent()) / 2;
            g2.setColor(fgColor);
            g2.drawString(getText(), textX, textY);

            g2.dispose();
        }

        @Override
        public void updateUI() {
            // Prevent LAF resets
        }
    }

    // Custom password field that draws black circles instead of '*'
    static class CustomPasswordField extends JPasswordField {
        private static final char OBF_CHAR = '\u26AB';
        private boolean showPassword = false;

        public CustomPasswordField() {
            super();
            // Prevent default echo char drawing — we'll control rendering ourselves.
            super.setEchoChar(OBF_CHAR);
            setFont(getFont());
            // Make caret visible when hiding/showing (default behaviour preserved)
            setCaretColor(Color.BLACK);
        }

        public void setShowPassword(boolean show) {
            this.showPassword = show;

            if (show) {
                super.setEchoChar((char) 0); // show plain text
            } else {
                super.setEchoChar(OBF_CHAR);
            }

            repaint();
        }

        @Override
        public Font getFont() {
            String os = System.getProperty("os.name").toLowerCase();
            String fontName;

            if (os.contains("win")) {
                fontName = "Segoe UI Symbol";
            } else if (os.contains("mac")) {
                fontName = "Apple Symbols";
            } else {
                fontName = "DejaVu Sans"; // Linux
            }

            return new Font(fontName, Font.PLAIN, 14);
        }

        // Important: prevent external callers from changing the echo char (we control
        // it)
        @Override
        public void setEchoChar(char c) {
            // no-op to avoid accidental changes
        }
    }

    // Rounded text fields and buttons border
    static class RoundedBorder extends AbstractBorder {
        private static final long serialVersionUID = 1L;
        private int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(BORDER_COLOR);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }

    // Rounded main panel
    static class RoundedPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private int radius;
        private Color borderColor;
        private Color backgroundColor;

        RoundedPanel(int radius, Color bgColor) {
            super();
            this.radius = radius;
            this.backgroundColor = bgColor;
            this.borderColor = bgColor;
            ;
            setOpaque(false);
        }

        RoundedPanel(int radius, Color bgColor, Color borderColor) {
            this(radius, bgColor);
            this.borderColor = borderColor;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(borderColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.setColor(backgroundColor);
            g2.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, radius, radius);
            super.paintComponent(g);
        }
    }
}