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
package com.gluonhq.jfxapps.metadata.test;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class ComponentCusto {

    @JsonInclude(Include.NON_NULL)
    private String category;
    @JsonInclude(Include.NON_NULL)
    private boolean resizeWhenTop = false;
    @JsonInclude(Include.NON_NULL)
    private Map<String, Qualifier> qualifiers;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean getResizeWhenTop() {
        return resizeWhenTop;
    }

    public void setResizeWhenTop(boolean resizeWhenTop) {
        this.resizeWhenTop = resizeWhenTop;
    }

    public Map<String, Qualifier> getQualifiers() {
        return qualifiers;
    }

    public void setQualifiers(Map<String, Qualifier> qualifiers) {
        this.qualifiers = qualifiers;
    }

    public static ComponentCusto DEFAULT() {
        return builder()
                .qualifier("default",
                        Qualifier.builder().fxml("TOBEDEFINED").image("TOBEDEFINED").imagex2("TOBEDEFINED").build())
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String category;
        private boolean resizeWhenTop = false;
        private Map<String, Qualifier> qualifiers;

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public Builder resizeWhenTop(Boolean resizeWhenTop) {
            this.resizeWhenTop = resizeWhenTop;
            return this;
        }

        public Builder qualifiers(Map<String, Qualifier> qualifiers) {
            this.qualifiers = qualifiers;
            return this;
        }

        public Builder qualifier(String key, Qualifier qualifier) {
            if (this.qualifiers == null) {
                this.qualifiers = new HashMap<>();
            }
            this.qualifiers.put(key, qualifier);
            return this;
        }

        public ComponentCusto build() {
            ComponentCusto componentCusto = new ComponentCusto();
            componentCusto.setCategory(category);
            componentCusto.setResizeWhenTop(resizeWhenTop);
            componentCusto.setQualifiers(qualifiers);
            return componentCusto;
        }
    }

    public static class Qualifier {

        private String fxml;
        private String image;
        private String imagex2;

        public String getFxml() {
            return fxml;
        }

        public void setFxml(String fxml) {
            this.fxml = fxml;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getImagex2() {
            return imagex2;
        }

        public void setImagex2(String imagex2) {
            this.imagex2 = imagex2;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private String fxml;
            private String image;
            private String imagex2;

            public Builder fxml(String fxml) {
                this.fxml = fxml;
                return this;
            }

            public Builder image(String image) {
                this.image = image;
                return this;
            }

            public Builder imagex2(String imagex2) {
                this.imagex2 = imagex2;
                return this;
            }

            public Qualifier build() {
                Qualifier qualifier = new Qualifier();
                qualifier.setFxml(fxml);
                qualifier.setImage(image);
                qualifier.setImagex2(imagex2);
                return qualifier;
            }
        }
    }
}
