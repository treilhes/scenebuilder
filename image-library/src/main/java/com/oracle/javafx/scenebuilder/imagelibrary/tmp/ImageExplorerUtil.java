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
package com.oracle.javafx.scenebuilder.imagelibrary.tmp;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.fontbox.ttf.CmapLookup;
import org.apache.fontbox.ttf.OTFParser;
import org.apache.fontbox.ttf.OpenTypeFont;
import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oracle.javafx.scenebuilder.api.library.LibraryFilter;
import com.oracle.javafx.scenebuilder.imagelibrary.tmp.ImageReportEntry.Type;
import com.oracle.javafx.scenebuilder.library.editor.panel.library.LibraryUtil;

public class ImageExplorerUtil {

    private static final Logger logger = LoggerFactory.getLogger(ImageExplorerUtil.class);
    
    private ImageExplorerUtil() {}
    
    public static String makeResourceName(String entryName) {
        return entryName.replace("\\", "/"); //NOI18N
    }

    public static ImageReportEntry exploreEntry(String entryName, ClassLoader classLoader, String resourceName, List<LibraryFilter> filters) {
        if (entryName == null || resourceName == null) {
            return null;
        }
        filters = filters == null ? new ArrayList<>() : filters;
        
        ImageReportEntry.Status status;
        Throwable entryException;
        List<Integer> unicodePoints = null;
        String fontName = null;
        BoundingBox boundingBox = null;
        Type type = null; 
        // Filtering out what starts with com.javafx. is bound to DTL-6378.
        if (filters.stream().anyMatch(f -> f.isFiltered(resourceName))) { //NOI18N
            status = ImageReportEntry.Status.IGNORED;
            entryException = null;
        } else {
            if (LibraryUtil.hasExtension(entryName, ImageLibrary.HANDLED_IMAGE_EXTENSIONS)) {
                try {
                    String upperEntry = entryName.toUpperCase();
                    if (upperEntry.toLowerCase().endsWith("." + ImageLibrary.TTF_EXTENSION)) {
                        TTFParser p = new TTFParser();
                        TrueTypeFont result = p.parse(classLoader.getResourceAsStream(makeResourceName(entryName)));
                        type = Type.FONT_ICONS;
                        fontName = result.getName();
                        unicodePoints = extractUnicodePoints(result);
                    } else if (upperEntry.toLowerCase().endsWith("." + ImageLibrary.OTF_EXTENSION)) {
                        OTFParser p = new OTFParser();
                        OpenTypeFont result = p.parse(classLoader.getResourceAsStream(makeResourceName(entryName)));
                        type = Type.FONT_ICONS;
                        fontName = result.getName();
                        unicodePoints = extractUnicodePoints(result);
                    } else {
                        // Checking each image for jars is too cpu costly
                        //BufferedImage img = ImageIO.read(classLoader.getResourceAsStream(makeResourceName(entryName)));
                        //boundingBox = new BoundingBox(0, 0, img.getWidth(), img.getHeight());
                        type = Type.IMAGE;
                    }
                    status = ImageReportEntry.Status.OK;
                    entryException = null;
                } catch (RuntimeException | IOException | Error x) {
                    status = ImageReportEntry.Status.KO;
                    entryException = x;
                }
            } else {
                status = ImageReportEntry.Status.IGNORED;
                entryException = null;
            }
        }
        
        ImageReportEntry result = new ImageReportEntry(entryName, status, entryException, type, resourceName);
        
        if (fontName != null && type == Type.FONT_ICONS) {
            result.setFontName(fontName);
        }
        if (unicodePoints != null && type == Type.FONT_ICONS) {
            result.getUnicodePoints().addAll(unicodePoints);
        }
        if (boundingBox != null && type == Type.IMAGE) {
            result.setBoundingBox(boundingBox);
        }
        return result;
    }
        
    public static ImageReportEntry exploreFile(Path file, String resourceName, ClassLoader classLoader) {
        ImageReportEntry.Status status;
        Throwable entryException;
        List<Integer> unicodePoints = null;
        String fontName = null;
        BoundingBox boundingBox = null;
        Type type = null; 
        
        if (LibraryUtil.hasExtension(file, ImageLibrary.HANDLED_IMAGE_EXTENSIONS)) {
            try (FileInputStream fis = new FileInputStream(file.toFile())){
                String upperEntry = file.toString().toLowerCase();
                if (upperEntry.endsWith("." + ImageLibrary.TTF_EXTENSION)) {
                    TTFParser p = new TTFParser();
                    TrueTypeFont result = p.parse(fis);
                    type = Type.FONT_ICONS;
                    fontName = result.getName();
                    unicodePoints = extractUnicodePoints(result);
                } else if (upperEntry.endsWith("." + ImageLibrary.OTF_EXTENSION)) {
                    OTFParser p = new OTFParser();
                    OpenTypeFont result = p.parse(fis);
                    type = Type.FONT_ICONS;
                    fontName = result.getName();
                    unicodePoints = extractUnicodePoints(result);
                } else {
                    //BufferedImage img = ImageIO.read(fis);
                    //boundingBox = new BoundingBox(0, 0, img.getWidth(), img.getHeight());
                    type = Type.IMAGE;
                }
                status = ImageReportEntry.Status.OK;
                entryException = null;
            } catch (RuntimeException | IOException | Error x) {
                status = ImageReportEntry.Status.KO;
                entryException = x;
            }
        } else {
            status = ImageReportEntry.Status.IGNORED;
            entryException = null;
        }

        if (entryException != null) {
            logger.warn("Exception while exploring entry {}", file, entryException);
        }
        
        ImageReportEntry result = new ImageReportEntry(file.getFileName().toString(), status, entryException, type, resourceName);
        
        if (fontName != null && type == Type.FONT_ICONS) {
            result.setFontName(fontName);
        }
        if (unicodePoints != null && type == Type.FONT_ICONS) {
            result.getUnicodePoints().addAll(unicodePoints);
        }
        if (boundingBox != null && type == Type.IMAGE) {
            result.setBoundingBox(boundingBox);
        }
        return result;
    }
    
    private static List<Integer> extractUnicodePoints(TrueTypeFont font) throws IOException {
        List<Integer> unicodePoints = new ArrayList<>();
        
        int numGlyph = font.getGlyph().getGlyphs().length;
        CmapLookup cmap = font.getUnicodeCmapLookup();
        
        for (int i=0;i<numGlyph;i++) {
            List<Integer> list = cmap.getCharCodes(i);
            unicodePoints.addAll(list);
        }
        
        return unicodePoints;
    }
    
    public static String unicodePointToXmlEntity(Integer unicodePoint) {
        return "&#x" + Integer.toHexString(unicodePoint) + ";"; 
    }
}
