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
package com.oracle.javafx.scenebuilder.metadata.custo;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class ComponentCusto {

    @JsonInclude(Include.NON_NULL)
    private String category;
    @JsonInclude(Include.NON_NULL)
    private boolean resizeNeededWhenTop = false;
    @JsonInclude(Include.NON_NULL)
    private String labelMutation;
    @JsonInclude(Include.NON_NULL)
    private String descriptionProperty;
    @JsonInclude(Include.NON_NULL)
    private Map<String, Qualifier> qualifiers = new HashMap<>();


    public ComponentCusto() {
        super();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isResizeNeededWhenTop() {
        return resizeNeededWhenTop;
    }

    public void setResizeNeededWhenTop(boolean resizeNeededWhenTop) {
        this.resizeNeededWhenTop = resizeNeededWhenTop;
    }

    public Map<String, Qualifier> getQualifiers() {
        return qualifiers;
    }

    public void setQualifiers(Map<String, Qualifier> qualifiers) {
        this.qualifiers = qualifiers;
    }

    public String getLabelMutation() {
        return labelMutation;
    }

    public void setLabelMutation(String labelMutation) {
        this.labelMutation = labelMutation;
    }

    public String getDescriptionProperty() {
        return descriptionProperty;
    }

    public void setDescriptionProperty(String descriptionProperty) {
        this.descriptionProperty = descriptionProperty;
    }

    public static class Qualifier {

        @JsonInclude(Include.NON_NULL)
        private String fxml;
        @JsonInclude(Include.NON_NULL)
        private String image;
        @JsonInclude(Include.NON_NULL)
        private String imagex2;
        @JsonInclude(Include.NON_NULL)
        private String displayName;
        @JsonInclude(Include.NON_NULL)
        private String label;
        @JsonInclude(Include.NON_NULL)
        private String lambdaCheck;

        public String getFxml() {
            return fxml;
        }

        public String getImage() {
            return image;
        }

        public String getImagex2() {
            return imagex2;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getLabel() {
            return label;
        }

        public String getLambdaCheck() {
            return lambdaCheck;
        }

        public void setFxml(String fxml) {
            this.fxml = fxml;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public void setImagex2(String imagex2) {
            this.imagex2 = imagex2;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public void setLambdaCheck(String lambdaCheck) {
            this.lambdaCheck = lambdaCheck;
        }

    }
}
