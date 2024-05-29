/*
 * Copyright (c) 2016, 2022, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2022, Pascal Treilhes and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.imagelibrary.library;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.gluonhq.jfxapps.core.api.library.ReportEntry.Status;
import com.oracle.javafx.scenebuilder.library.api.Transform;

public class ImageFilterTransform implements Transform<ImageReport, ImageReport> {
    Map<String, StandardImage> imageSourceData = new HashMap<>();
    Map<String, FontImage> fontSourceData = new HashMap<>();

    public ImageFilterTransform() {
        super();
    }

    public FontImage getOrCreateFontImage(String fontName) {
        FontImage fontImage = fontSourceData.get(fontName);

        if (fontImage == null) {
            fontImage = new FontImage(fontName);
            fontSourceData.put(fontName, fontImage);
        }

        return fontImage;
    }

    public FontImageItem getOrCreateFontImageItem(FontImage fontImage, Integer unicodePoint) {
        assert fontSourceData.containsValue(fontImage);

        FontImageItem fontImageItem = fontImage.getItems().get(unicodePoint);

        if (fontImageItem == null) {
            fontImageItem = new FontImageItem(unicodePoint);
            fontImage.getItems().put(unicodePoint, fontImageItem);
        }

        return fontImageItem;
    }

    public StandardImage getOrCreateStandardImage(String imageName) {
        StandardImage standardImage = imageSourceData.get(imageName);

        if (standardImage == null) {
            standardImage = new StandardImage(imageName);
            imageSourceData.put(imageName, standardImage);
            getOrCreateStandardImageItem(standardImage, BoundingBox.FULL).setImported(true);
        }

        return standardImage;
    }

    public StandardImageItem getOrCreateStandardImageItem(StandardImage standardImage, BoundingBox boundingBox) {
        assert imageSourceData.containsValue(standardImage);
        String key = boundingBox.toString();
        StandardImageItem standardImageItem = standardImage.getItems().get(key);

        if (standardImageItem == null) {
            standardImageItem = new StandardImageItem(boundingBox);
            standardImage.getItems().put(key, standardImageItem);
        }

        return standardImageItem;
    }

    @Override
    public List<ImageReport> filter(List<ImageReport> inputs) {
        return inputs.stream().map(r -> {
            try {

                ImageReport filteredReport = new ImageReport(r.getSource());

                r.getEntries().forEach(e -> {
                    if (e.getStatus() == Status.OK) {

                        switch (e.getType()) {
                        case FONT_ICONS:
                            FontImage fontSource = fontSourceData.get(e.getFontName());

                            if (fontSource != null && fontSource.isImported()) {
                                e.getUnicodePoints().forEach(up -> {
                                    FontImageItem item = fontSource.getItems().get(up);
                                    if (item != null && item.isImported()) {
                                        ImageReportEntry clone = e.clone();
                                        clone.getUnicodePoints().add(up);

//                                            if (item.getName() != null && !item.getName().isEmpty()) {
//                                                clone.setName(item.getName());
//                                            }

                                        filteredReport.getEntries().add(clone);
                                    }
                                });
                            }
                            break;
                        case IMAGE:
                            StandardImage imageSource = imageSourceData.get(e.getName());

                            if (imageSource != null && imageSource.isImported()) {
                                imageSource.getItems().values().forEach(item -> {
                                    if (item != null && item.isImported()) {
                                        ImageReportEntry clone = e.clone();
                                        clone.setBoundingBox(item.getBoundingBox().clone());

//                                            if (item.getName() != null && !item.getName().isEmpty()) {
//                                                clone.setName(item.getName());
//                                            }

                                        filteredReport.getEntries().add(clone);
                                    }
                                });
                            }
                            break;
                        default:
                            break;
                        }

                    }
                });
                return filteredReport;
            } catch (Exception e) {
                return r;
            }
        }).filter(r -> !r.getEntries().isEmpty()).collect(Collectors.toList());

    }

    public Map<String, StandardImage> getImageSourceData() {
        return imageSourceData;
    }

    public void setImageSourceData(Map<String, StandardImage> imageSourceData) {
        this.imageSourceData = imageSourceData;
    }

    public Map<String, FontImage> getFontSourceData() {
        return fontSourceData;
    }

    public void setFontSourceData(Map<String, FontImage> fontSourceData) {
        this.fontSourceData = fontSourceData;
    }

    public static class FontImage {

        private String fontName;
        private boolean imported = true;
        private Map<Integer, FontImageItem> items = new HashMap<>();

        public FontImage() {
            super();
        }

        public FontImage(String fontName) {
            super();
            this.fontName = fontName;
        }

        public String getFontName() {
            return fontName;
        }

        public boolean isImported() {
            return imported;
        }

        public Map<Integer, FontImageItem> getItems() {
            return items;
        }

        private void setFontName(String fontName) {
            this.fontName = fontName;
        }

        private void setItems(Map<Integer, FontImageItem> items) {
            this.items = items;
        }

        public void setImported(boolean imported) {
            this.imported = imported;
        }

    }

    public static class FontImageItem {

        private Integer unicodePoint;
        private boolean imported = false;
        private String name;

        public FontImageItem(Integer unicodePoint) {
            super();
            this.unicodePoint = unicodePoint;
        }

        public FontImageItem() {
            super();
        }

        public boolean isImported() {
            return imported;
        }

        public void setImported(boolean imported) {
            this.imported = imported;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getUnicodePoint() {
            return unicodePoint;
        }

        private void setUnicodePoint(Integer unicodePoint) {
            this.unicodePoint = unicodePoint;
        }

    }

    public static class StandardImage {

        private String imageName;
        private boolean imported = true;
        private Map<String, StandardImageItem> items = new HashMap<>();

        public StandardImage() {
            super();
        }

        public StandardImage(String imageName) {
            super();
            this.imageName = imageName;
        }

        public boolean isImported() {
            return imported;
        }

        public void setImported(boolean imported) {
            this.imported = imported;
        }

        public String getImageName() {
            return imageName;
        }

        public Map<String, StandardImageItem> getItems() {
            return items;
        }

        private void setImageName(String imageName) {
            this.imageName = imageName;
        }

        private void setItems(Map<String, StandardImageItem> items) {
            this.items = items;
        }

    }

    public static class StandardImageItem {

        private BoundingBox boundingBox;
        private boolean imported = true;
        private String name;

        public StandardImageItem() {
            super();
        }

        public StandardImageItem(BoundingBox boundingBox) {
            super();
            this.boundingBox = boundingBox;
        }

        public boolean isImported() {
            return imported;
        }

        public void setImported(boolean imported) {
            this.imported = imported;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public BoundingBox getBoundingBox() {
            return boundingBox;
        }

        private void setBoundingBox(BoundingBox boundingBox) {
            this.boundingBox = boundingBox;
        }

    }

}
