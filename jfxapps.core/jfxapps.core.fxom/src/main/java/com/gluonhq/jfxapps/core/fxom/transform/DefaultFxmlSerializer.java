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
package com.gluonhq.jfxapps.core.fxom.transform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.FXOMProperty;
import com.gluonhq.jfxapps.core.fxom.collector.DeclaredClassCollector;
import com.gluonhq.jfxapps.core.fxom.collector.PropertyCollector;
import com.gluonhq.jfxapps.core.fxom.glue.GlueDocument;
import com.gluonhq.jfxapps.core.fxom.glue.GlueInstruction;

import javafx.fxml.FXMLLoader;

/**
 *
 *
 */
public class DefaultFxmlSerializer implements FXOMSerializer {

    private static final String IMPORT = "import"; // NOI18N
    private static final boolean DEFAULT_WILDCARD_IMPORT = false;
    private static final boolean DEFAULT_COMPRESS = false;

    private final boolean wildcardImports;
    private final String javafxVersion;
    private final boolean compress;

    /**
     * Returns the FXML string representation of the FXOMDocument.
     * @param wildcardImports If the FXML should have wildcards in its imports.
     * @return The FXML string representation. This can be empty if current root is null.
     */
    public DefaultFxmlSerializer(
            Boolean wildcardImports,
            String javafxVersion,
            Boolean compress) {
        this.wildcardImports = wildcardImports != null ? wildcardImports : DEFAULT_WILDCARD_IMPORT;
        this.javafxVersion = javafxVersion != null ? javafxVersion : FXMLLoader.JAVAFX_VERSION;
        this.compress = compress != null ? compress : DEFAULT_COMPRESS;
    }

    public DefaultFxmlSerializer() {
        this(null, null, null);
    }

    /**
     * Returns the FXML string representation of the FXOMDocument.
     * @param fxomDocument The FXOMDocument to serialize.
     * @return The string representation. This can be empty if current root is null.
     */
    @Override
    public String serialize(FXOMDocument fxomDocument) {

        if (fxomDocument.getFxomRoot() == null) {
            assert fxomDocument.getGlue() != null;
            assert fxomDocument.getGlue().getMainElement() == null;
            assert fxomDocument.getSceneGraphRoot() == null;
            return "";
        } else {
            assert fxomDocument != null;
            assert fxomDocument.getGlue() != null;
            assert fxomDocument.getGlue().getMainElement() != null;
            // Note that sceneGraphRoot might be null if fxomRoot is unresolved

            //FIXME the autoindent has been commented out because it made the comment test failing
            // It is just for convenience and must be fixed later
            //fxomDocument.getGlue().updateIndent();

            updateNameSpace(fxomDocument, javafxVersion);
            updateImportInstructions(fxomDocument);

            return fxomDocument.getGlue().toString(compress);
        }
    }

    /*
     * Private
     */

    private static final String NAME_SPACE_FX_FORMAT = "http://javafx.com/javafx/%s"; // NOI18N
    private static final String NAME_SPACE_FXML = "http://javafx.com/fxml/1"; // NOI18N

    private void updateNameSpace(FXOMDocument fxomDocument, String javafxVersion) {
        assert fxomDocument.getFxomRoot() != null;

        final FXOMObject fxomRoot = fxomDocument.getFxomRoot();
        final String currentNameSpaceFX = fxomRoot.getNameSpaceFX();
        final String currentNameSpaceFXML = fxomRoot.getNameSpaceFXML();

        String nameSpaceFx = String.format(NAME_SPACE_FX_FORMAT, javafxVersion);
        if ((currentNameSpaceFX == null) || (!currentNameSpaceFX.equals(nameSpaceFx))) {
            fxomRoot.setNameSpaceFX(nameSpaceFx);
        }

        if ((currentNameSpaceFXML == null) || (!currentNameSpaceFXML.equals(NAME_SPACE_FXML))) {
            fxomRoot.setNameSpaceFXML(NAME_SPACE_FXML);
        }

    }

    private void updateImportInstructions(FXOMDocument fxomDocument) {
        assert fxomDocument.getFxomRoot() != null;

        // gets list of the imports to be added to the FXML document.
        List<GlueInstruction> importList = getHeaderIncludes(fxomDocument);

        // synchronizes the glue with the list of glue instructions
        synchronizeHeader(fxomDocument.getGlue(), importList);
    }

    private List<GlueInstruction> getHeaderIncludes(FXOMDocument fxomDocument) {
        // TODO: When wildcardImport is true, add package name only when no of classes
        // which belong to the same package exceed 3

        // constructs the set of classes to be imported. No duplicates allowed.
        final Set<String> imports = new TreeSet<>(); // Sorted

        // gets list of declared classes, declared classes are the ones directly used as
        // a Node.
        // Example: <Button/> ; classname = javafx.scene.control.Button
        fxomDocument.getFxomRoot().collect(DeclaredClassCollector.all())
                .forEach(dc -> imports.add(wildcardImports ? dc.getPackageName() + ".*" : dc.getCanonicalName()));

        FXOMObject root = fxomDocument.getFxomRoot();

        imports.addAll(findPropertyClasses(root.getChildObjects().toArray(FXOMObject[]::new)));
        imports.addAll(findPropertyClasses(root));

        return createGlueInstructionsForImports(fxomDocument, imports);
    }

    private Set<String> findPropertyClasses(FXOMObject... fxomObjects) {
        return Arrays.stream(fxomObjects).map(l -> l.collect(PropertyCollector.allSimpleProperties())) // list of lists
                                                                                                       // containing
                                                                                                       // FXOMProperties
                .flatMap(Collection::stream) // add all to one list of FXOMProperties
                .map(FXOMProperty::getName) // list of all PropertyNames
                .filter(prop -> prop.getResidenceClass() != null) // filter for ResidenceClass (used for static methods
                                                                  // example: HBox.hgrow="..")
                .map(prop -> wildcardImports ? prop.getResidenceClass().getPackageName() + ".*"
                        : prop.getResidenceClass().getName()) // list of classes // NOI18N
                .collect(Collectors.toSet());
    }

    // Creates a List of glue instruction for all imported classes.
    private List<GlueInstruction> createGlueInstructionsForImports(FXOMDocument fxomDocument, Set<String> imports) {
        List<GlueInstruction> importsList = new ArrayList<>();
        imports.forEach(name -> {
            final GlueInstruction instruction = new GlueInstruction(fxomDocument.getGlue(), IMPORT, name);
            importsList.add(instruction);
        });
        return importsList;
    }

    private void synchronizeHeader(GlueDocument glue, List<GlueInstruction> importList) {
        synchronized (this) {
            // find out where the first import instruction is located
            final int firstImportIndex;
            List<GlueInstruction> existingImports = glue.collectInstructions(IMPORT);
            if (existingImports.isEmpty()) {
                firstImportIndex = 0;
            } else {
                GlueInstruction firstImport = existingImports.get(0);
                firstImportIndex = glue.getContent().indexOf(firstImport);
            }

            // remove previously defined imports and leave all other things (like comments
            // and such) intact
            glue.getContent().removeIf(glueAuxiliary -> glueAuxiliary instanceof GlueInstruction
                    && IMPORT.equals(((GlueInstruction) glueAuxiliary).getTarget()));

            // insert the import instructions at the first import index
            glue.getContent().addAll(firstImportIndex, importList);
        }
    }
}
