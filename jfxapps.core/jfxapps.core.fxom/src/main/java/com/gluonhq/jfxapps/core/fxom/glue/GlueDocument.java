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
package com.gluonhq.jfxapps.core.fxom.glue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 */
public class GlueDocument extends GlueNode {

    private final List<GlueNode> content = new ArrayList<>();
    private GlueElement mainElement;
    //private final List<GlueAuxiliary> header = new ArrayList<>();

    public GlueDocument() {
    }

    public GlueDocument(String xmlText) throws IOException {
        assert xmlText != null;
        if (isEmptyXmlText(xmlText) == false) {
            final GlueLoader loader = new GlueLoader(this);
            loader.load(xmlText);
            adjustMainElementIndentation();
        }
    }

    public GlueElement getMainElement() {
        return mainElement;
    }

    public void setMainElement(GlueElement newMainElement) {
        if ((newMainElement != null) && (newMainElement.getParent() != null)) {
            newMainElement.removeFromParent();
        }

        if (content.contains(this.mainElement)) {
            int index = content.indexOf(this.mainElement);
            content.remove(index);
            content.add(index, newMainElement);
        } else {
            content.add(newMainElement);
        }

        this.mainElement = newMainElement;
    }

    public List<GlueNode> getContent() {
        return content;
    }

    public void updateIndent() {
        if (mainElement != null) {
            mainElement.updateIndent(0);
        }
    }


    public void addHeader(GlueNode node) {
        if (mainElement != null) {
            assert content.indexOf(mainElement) != -1;
            content.add(content.indexOf(mainElement), node);
        } else {
            content.add(node);
        }
    }

    /*
     * Utilities
     */

    public List<GlueInstruction> collectInstructions(String target) {
        final List<GlueInstruction> result = new ArrayList<>();

        assert target != null;

        for (GlueNode node : content) {
            if (node instanceof GlueInstruction) {
                final GlueInstruction i = (GlueInstruction) node;
                if (target.equals(i.getTarget())) {
                    result.add(i);
                }
            }
        }

        return result;
    }

    public List<GlueElement> collectHeaderElements() {
        final List<GlueElement> result = new ArrayList<>();

        for (GlueNode node : content) {
            if (node instanceof GlueElement) {
                if (node == getMainElement()) {
                    return result;
                }
                final GlueElement i = (GlueElement) node;
                result.add(i);
            }
        }

        return result;
    }


    public List<GlueElement> collectFooterElements() {
        final List<GlueElement> result = new ArrayList<>();
        boolean collect = false;
        for (GlueNode node : content) {
            if (node instanceof GlueElement) {
                if (collect) {
                    final GlueElement i = (GlueElement) node;
                    result.add(i);
                }

                if (node == getMainElement()) {
                    collect = true;
                }
            }
        }
        return result;
    }

    public static boolean isEmptyXmlText(String xmlText) {
        assert xmlText != null;
        return xmlText.trim().isEmpty();
    }

    /*
     * Object
     */

    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(boolean compress) {
        final String result;
        if (mainElement == null) {
            result = ""; //NOCHECK
        } else {
            final GlueSerializer serializer = new GlueSerializer(this);
            result = serializer.toString(compress);
        }
        return result;
    }


    /*
     * Private
     */

    private void adjustMainElementIndentation() {
        /*
         * By default, if a root element is empty and expressed like this:
         *     <AnchorPane />
         * indentation logic would keep the upcoming children on the same line:
         *     <AnchorPane> <children> <Button/> </children> </AnchorPane>.
         *
         * With the adjustment below, indentation logic will produce:
         *     <AnchorPane>
         *        <children>
         *           <Button />
         *        </children>
         *     </AnchorPane>
         */

        if (mainElement != null) {
            if (mainElement.getFront().isEmpty()) {
                mainElement.getFront().add(new GlueCharacters(this, "\n")); //NOCHECK
            }
            if (mainElement.getTail().isEmpty() && mainElement.getChildren().isEmpty()) {
                mainElement.getTail().add(new GlueCharacters(this, "\n")); //NOCHECK
            }
        }
    }

    public GlueCursor cursor() {
        return new GlueCursor(this);
    }
}
