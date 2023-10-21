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
package com.oracle.javafx.scenebuilder.editors.popupeditors;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.scenebuilder.fxml.api.Documentation;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.core.context.SbContext;
import com.oracle.javafx.scenebuilder.api.factory.AbstractFactory;
import com.oracle.javafx.scenebuilder.api.fs.FileSystem;
import com.oracle.javafx.scenebuilder.api.ui.dialog.Dialog;
import com.oracle.javafx.scenebuilder.api.ui.misc.MessageLogger;
import com.oracle.javafx.scenebuilder.api.util.FXMLUtils;
import com.oracle.javafx.scenebuilder.core.editors.AutoSuggestEditor;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.util.EditorUtils;
import com.oracle.javafx.scenebuilder.editors.control.BoundedDoubleEditor;
import com.oracle.javafx.scenebuilder.fxml.api.selection.SelectionState;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;

/**
 * Font popup editor.
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class FontPopupEditor extends PopupEditor {

    private final static Map<String, Object> fontSizeConstants = new HashMap<>();
    static {
        fontSizeConstants.put(" 9", 9.0);
        fontSizeConstants.put("10", 10.0);
        fontSizeConstants.put("11", 11.0);
        fontSizeConstants.put("12", 12.0);
        fontSizeConstants.put("13", 13.0);
        fontSizeConstants.put("14", 14.0);
        fontSizeConstants.put("18", 18.0);
        fontSizeConstants.put("24", 24.0);
        fontSizeConstants.put("36", 36.0);
        fontSizeConstants.put("48", 48.0);
        fontSizeConstants.put("64", 64.0);
        fontSizeConstants.put("72", 72.0);
        fontSizeConstants.put("96", 96.0);
    }

    private final static DoublePropertyMetadata.CustomizableDoublePropertyMetadata sizeMetadata =
            new DoublePropertyMetadata.CustomizableDoublePropertyMetadata.Builder()
                .withName(PropertyName.EMPTY)
                .withReadWrite(true)
                .withMin(1.0)
                .withMax(96.0)
                .withLenientBoundary(true)
                .withConstants(fontSizeConstants).build();

    @FXML
    private StackPane familySp;
    @FXML
    private StackPane styleSp;
    @FXML
    private StackPane sizeSp;

    private Parent root;
    private Font font = Font.getDefault();
    private FontFamilyEditor familyEditor;
    private FontStyleEditor fontStyleEditor;
    private final BoundedDoubleEditor sizeEditor;

    private final MessageLogger messageLogger;

    private final FontStyleEditor.Factory fontStyleEditorFactory;
    private final FontFamilyEditor.Factory fontFamilyEditorFactory;

    public FontPopupEditor(
            Dialog dialog,
            Documentation documentation,
            FileSystem fileSystem,
            MessageLogger messageLogger,
            BoundedDoubleEditor sizeEditor,
            FontStyleEditor.Factory fontStyleEditorFactory,
            FontFamilyEditor.Factory fontFamilyEditorFactory
            ) {
        super(dialog, documentation, fileSystem);
        this.messageLogger = messageLogger;
        this.sizeEditor = sizeEditor;
        this.fontStyleEditorFactory = fontStyleEditorFactory;
        this.fontFamilyEditorFactory = fontFamilyEditorFactory;
    }

//    private void initialize(Editor editorController) {
//        this.editorController = editorController;
//    }

    private void setStyle() {
        fontStyleEditor.reset(new ArrayList<>(getStyles(EditorUtils.toString(familyEditor.getValue()), false, messageLogger)));//NOCHECK
        fontStyleEditor.setUpdateFromModel(true);
        fontStyleEditor.setValue(font.getStyle());
        fontStyleEditor.setUpdateFromModel(false);
    }

    private void commit() {
        if (isUpdateFromModel()) {
            return;
        }
        font = getFont();
        assert font != null;
//        System.out.println("Committing: " + font + " - preview: " + getValueAsString());
        commitValue(font);
    }

    private Font getFont() {
        Font oldFont = font;
        Object sizeObj = sizeEditor.getValue();
        assert sizeObj instanceof Double;
        Font newFont = getFont(EditorUtils.toString(familyEditor.getValue()), EditorUtils.toString(fontStyleEditor.getValue()),
                (Double) sizeObj, messageLogger);
        if (newFont != null) {
            return newFont;
        } else {
            return oldFont;
        }
    }

    @Override
    public Object getValue() {
        return font;
    }

    @Override
    public void reset(ValuePropertyMetadata propMeta, SelectionState selectionState) {
        super.reset(propMeta, selectionState);

    }

    //
    // Interface from PopupEditor
    // Methods called by PopupEditor.
    //
    @Override
    public void initializePopupContent() {
        root = FXMLUtils.load(this, "FontPopupEditor.fxml");
        // Add the editors in the scene graph

        //TODO remove those editor factories and use metadata like sizeEditor
        familyEditor = fontFamilyEditorFactory.getEditor("", "", getFamilies(messageLogger));//NOCHECK
        familySp.getChildren().add(familyEditor.getValueEditor());

        //TODO remove those editor factories and use metadata like sizeEditor
        fontStyleEditor = fontStyleEditorFactory.getEditor("", "", new ArrayList<>());//NOCHECK
        styleSp.getChildren().add(fontStyleEditor.getValueEditor());


        sizeEditor.reset(sizeMetadata, null);

        commitOnFocusLost(sizeEditor);
        sizeSp.getChildren().add(sizeEditor.getValueEditor());

        familyEditor.valueProperty().addListener((ChangeListener<Object>) (ov, oldVal, newVal) -> {
            if (familyEditor.isUpdateFromModel()) {
                // nothing to do
                return;
            }
            commit();
            setStyle();
        });

        fontStyleEditor.valueProperty().addListener((ChangeListener<Object>) (ov, oldVal, newVal) -> {
            if (fontStyleEditor.isUpdateFromModel()) {
                // nothing to do
                return;
            }
            commit();
        });

        sizeEditor.valueProperty().addListener((ChangeListener<Object>) (ov, oldVal, newVal) -> {
            if (sizeEditor.isUpdateFromModel()) {
                // nothing to do
                return;
            }
            commit();
        });

        sizeEditor.transientValueProperty().addListener((ChangeListener<Object>) (ov, oldVal, newVal) -> transientValue(getFont()));
    }

    @Override
    public String getPreviewString(Object value) {
        // value should never be null
        assert value instanceof Font;
        Font fontVal = (Font) value;
        if (isIndeterminate()) {
            return "-"; //NOCHECK
        } else {
            String size = EditorUtils.valAsStr(fontVal.getSize());
            return fontVal.getFamily() + " " + size + "px" //NOCHECK
                    + (!fontVal.getName().equals(fontVal.getFamily()) && !"Regular".equals(fontVal.getStyle()) ? //NOCHECK
                    " (" + fontVal.getStyle() + ")" : ""); //NOCHECK
        }
    }

    @Override
    public void setPopupContentValue(Object value) {
        assert value instanceof Font;
        font = (Font) value;
        familyEditor.setUpdateFromModel(true);
        familyEditor.setValue(font.getFamily());
        familyEditor.setUpdateFromModel(false);
        setStyle();
        sizeEditor.setUpdateFromModel(true);
        sizeEditor.setValue(font.getSize());
        sizeEditor.setUpdateFromModel(false);
    }

    @Override
    public Node getPopupContentNode() {
        return root;
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
    public static class FontFamilyEditor extends AutoSuggestEditor {

        private final MessageLogger messageLogger;
        private List<String> families;
        private String family = null;

        public FontFamilyEditor(Dialog dialog,
                Documentation documentation,
                FileSystem fileSystem,
                MessageLogger messageLogger) {
            super(dialog, documentation, fileSystem);
            this.messageLogger = messageLogger;
        }

        private void initialize(String name, String defaultValue, List<String> families) {
            this.families = families;
            preInit(Type.ALPHA, families);
            EventHandler<ActionEvent> onActionListener = event -> {
                if (Objects.equals(family, getTextField().getText())) {
                    // no change
                    return;
                }
                family = getTextField().getText();
                if (family.isEmpty() || !FontFamilyEditor.this.families.contains(family)) {
                    messageLogger.logWarningMessage(
                            "inspector.font.invalidfamily", family); //NOCHECK
                    return;
                }
//                    System.out.println("Setting family from '" + valueProperty().get() + "' to '" + value + "'");
                valueProperty().setValue(family);
            };

            setTextEditorBehavior(this, getTextField(), onActionListener);
            commitOnFocusLost(this);
            this.reset(name, defaultValue);
        }

        @Override
        public Object getValue() {
            return getTextField().getText();
        }

        @SuppressWarnings("unused")
        public List<String> getFamilies() {
            return families;
        }

        @Component
        @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
        public static class Factory extends AbstractFactory<FontFamilyEditor> {
            public Factory(SceneBuilderBeanFactory sbContext) {
                super(sbContext);
            }

            public FontFamilyEditor getEditor(String name, String defaultValue, List<String> families) {
                return create(FontFamilyEditor.class, (e) -> e.initialize(name, defaultValue, families));
            }
        }
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
    public static class FontStyleEditor extends AutoSuggestEditor {

        private String style = null;
        private final MessageLogger messageLogger;

        public FontStyleEditor(Dialog dialog,
                Documentation documentation,
                FileSystem fileSystem,
                MessageLogger messageLogger) {
            super(dialog, documentation, fileSystem);
            this.messageLogger = messageLogger;
        }

        public void reset(List<String> suggestedList) {
            super.reset("", "", suggestedList);
        }

        private void initialize(String name, String defaultValue, List<String> suggestedList) {
            preInit(Type.ALPHA, suggestedList);

            EventHandler<ActionEvent> onActionListener = event -> {
                if (Objects.equals(style, getTextField().getText())) {
                    // no change
                    return;
                }
                style = getTextField().getText();
                if (style.isEmpty() || !getSuggestedList().contains(style)) {
                    messageLogger.logWarningMessage("inspector.font.invalidstyle", style); //NOCHECK
                    return;
                }
                valueProperty().setValue(style);
            };

            setTextEditorBehavior(this, getTextField(), onActionListener);
            commitOnFocusLost(this);

            this.reset(name, defaultValue);
        }

        @Override
        public Object getValue() {
            return getTextField().getText();
        }

        @Component
        @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
        public static class Factory extends AbstractFactory<FontStyleEditor> {
            public Factory(SceneBuilderBeanFactory sbContext) {
                super(sbContext);
            }

            public FontStyleEditor getEditor(String name, String defaultValue, List<String> suggestedList) {
                return create(FontStyleEditor.class, (e) -> e.initialize(name, defaultValue, suggestedList));
            }
        }
    }

    private static void commitOnFocusLost(AutoSuggestEditor autoSuggestEditor) {
        autoSuggestEditor.getTextField().focusedProperty().addListener((ChangeListener<Boolean>) (ov, oldVal, newVal) -> {
            if (!newVal) {
                autoSuggestEditor.getCommitListener().handle(null);
            }
        });
    }

    /*
     *
     * Utilities methods for Font handling
     *
     */
    private static WeakReference<Map<String, Map<String, Font>>> fontCache
            = new WeakReference<>(null);

    // Automagically discover which font will require the work around for RT-23021.
    private static volatile Map<String, String> pathologicalFonts = null;

    private static final Comparator<Font> fontComparator
            = (t, t1) -> {
        int cmp = t.getName().compareTo(t1.getName());
        if (cmp != 0) {
            return cmp;
        }
        return t.toString().compareTo(t1.toString());
    };

    public static Set<Font> getAllFonts() {
        Font f = Font.getDefault();
        double defSize = f.getSize();
        Set<Font> allFonts = new TreeSet<>(fontComparator);
        for (String familly : Font.getFamilies()) {
            //System.out.println("*** FAMILY: " + familly); //NOCHECK
            for (String name : Font.getFontNames(familly)) {
                Font font = new Font(name, defSize);
                allFonts.add(font);
                //System.out.println("\t\""+name+"\" -- name=\""+font.getName()+"\", familly=\""+font.getFamily()+"\", style=\""+font.getStyle()+"\""); //NOCHECK
            }
        }
        // some font will not appear with the code above: we also need to use getAllNames!
        for (String name : Font.getFontNames()) {
            Font font = new Font(name, defSize);
            allFonts.add(font);
        }
        return allFonts;
    }

    public static List<String> getFamilies(MessageLogger messageLogger) {
//        System.out.println("Getting font families...");
        return new ArrayList<>(getFontMap(messageLogger).keySet());
    }

    public static Set<String> getStyles(String family, boolean canBeUnknown, MessageLogger messageLogger) {
        Map<String, Font> styles = getFontMap(messageLogger).get(family);
        if (styles == null) {
            assert !canBeUnknown;
            styles = Collections.emptyMap();
        }
        return styles.keySet();
    }

    public static Font getFont(String family, String style, MessageLogger messageLogger) {
        Map<String, Font> styles = getFontMap(messageLogger).get(family);
        if (styles == null) {
            styles = Collections.emptyMap();
        }

        if (styles.get(style) == null) {
            // The requested style does not exist for this font:
            // pick up the first style
            style = styles.keySet().iterator().next();
        }
        return styles.get(style);
    }

    public static Font getFont(String family, String style, double size, MessageLogger messageLogger) {
        final Font font = getFont(family, style, messageLogger);
        if (font == null) {
            return null;
        }
        return getFont(font, size);
    }

    public static Font getFont(Font font, double size) {
        if (font == null) {
            assert false;
            font = Font.getDefault();
        }
        if (Math.abs(font.getSize() - size) < .0000001) {
            return font;
        }

        return new Font(getPersistentName(font), size);
    }

    public static Map<String, String> getPathologicalFonts() {
        if (pathologicalFonts == null) {
            final double size = Font.getDefault().getSize();
            final String defaultName = Font.getDefault().getName();
            Map<String, String> problems = new HashMap<>();
            final Set<String> allNames = new HashSet<>(Font.getFontNames());
            for (String familly : Font.getFamilies()) {
                allNames.addAll(Font.getFontNames(familly));
            }
            for (String name : allNames) {
                Font f = new Font(name, size);
                if (f.getName().equals(name)) {
                    continue;
                }
                if (f.getName().equals(defaultName) || f.getName().equals("System")) { //NOCHECK
                    continue; //NOCHECK
                }
                final Font f2 = new Font(f.getName(), size);
                if (f2.getName().equals(f.getName())) {
                    continue;
                }
                problems.put(f.getName(), name);
            }
            pathologicalFonts = Collections.unmodifiableMap(problems);
        }
        return pathologicalFonts;
    }

    public static String getPersistentName(Font font) {
        // The block below is an ugly workaround for
        // RT-23021: Inconsitent naming for fonts in the 'Tahoma' family.
        final Map<String, String> problems = getPathologicalFonts();
        if (problems.containsKey(font.getName())) { // e.g. font.getName() is "Tahoma Bold" //NOCHECK
            final Font test = new Font(font.getName(), font.getSize());
            if (test.getName().equals(font.getName())) {
                // OK
                return font.getName();
            } else {
                final String alternateName = problems.get(font.getName()); // e.g: "Tahoma Negreta" //NOCHECK
                assert alternateName != null;
                final Font test2 = new Font(alternateName, font.getSize()); //NOCHECK
                if (test2.getName().equals(font.getName())) {
                    // OK
                    return alternateName; // e.g: "Tahoma Negreta" //NOCHECK
                }
            }
        }
        return font.getName();
    }

    private static Map<String, Map<String, Font>> getFontMap(MessageLogger messageLogger) {
        Map<String, Map<String, Font>> fonts = fontCache.get();
        if (fonts == null) {
            fonts = makeFontMap(messageLogger);
            fontCache = new WeakReference<>(fonts);
        }
        return fonts;
    }

    private static Map<String, Map<String, Font>> makeFontMap(MessageLogger messageLogger) {
        final Set<Font> fonts = getAllFonts();
        final Map<String, Map<String, Set<Font>>> fontTree = new TreeMap<>();

        for (Font f : fonts) {
            Map<String, Set<Font>> familyStyleMap = fontTree.get(f.getFamily());
            if (familyStyleMap == null) {
                familyStyleMap = new TreeMap<>();
                fontTree.put(f.getFamily(), familyStyleMap);
            }
            Set<Font> styleFonts = familyStyleMap.get(f.getStyle());
            if (styleFonts == null) {
                styleFonts = new HashSet<>();
                familyStyleMap.put(f.getStyle(), styleFonts);
            }
            styleFonts.add(f);
        }

        final Map<String, Map<String, Font>> res = new TreeMap<>();
        for (Map.Entry<String, Map<String, Set<Font>>> e1 : fontTree.entrySet()) {
            final String family = e1.getKey();
            final Map<String, Set<Font>> styleMap = e1.getValue();
            final Map<String, Font> resMap = new TreeMap<>();
            for (Map.Entry<String, Set<Font>> e2 : styleMap.entrySet()) {
                final String style = e2.getKey();
                final Set<Font> fontSet = e2.getValue();
                int size = fontSet.size();
                assert 1 <= size;
                if (1 < size) {
                    messageLogger.logWarningMessage("inspector.font.samefamilystyle", styleMap.get(style)); //NOCHECK
                }
                resMap.put(style, styleMap.get(style).iterator().next());
            }
            res.put(family, Collections.<String, Font>unmodifiableMap(resMap));
        }
        return Collections.<String, Map<String, Font>>unmodifiableMap(res);
    }

    private static List<String> getPredefinedFontSizes() {
        String[] predefinedFontSizes
                = {"9", "10", "11", "12", "13", "14", "18", "24", "36", "48", "64", "72", "96"};//NOCHECK
        return Arrays.asList(predefinedFontSizes);
    }

}
