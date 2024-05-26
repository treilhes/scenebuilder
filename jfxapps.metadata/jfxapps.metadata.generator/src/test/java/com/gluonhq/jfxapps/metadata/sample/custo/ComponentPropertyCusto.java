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
package com.gluonhq.jfxapps.metadata.sample.custo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class ComponentPropertyCusto {
    @JsonInclude(Include.NON_NULL)
    private int order = -1;
    @JsonInclude(Include.NON_NULL)
    private String image;
    @JsonInclude(Include.NON_NULL)
    private String imagex2;
    @JsonInclude(Include.NON_NULL)
    private boolean freeChildPositioning = false;
    @JsonInclude(Include.NON_NULL)
    private String childLabelMutation;

    public int getOrder() {
        return order;
    }
    public String getImage() {
        return image;
    }
    public String getImagex2() {
        return imagex2;
    }

    public boolean isFreeChildPositioning() {
        return freeChildPositioning;
    }
    public String getChildLabelMutation() {
        return childLabelMutation;
    }
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int order;
        private String image;
        private String imagex2;
        private boolean freeChildPositioning = false;
        private String childLabelMutation;

        public Builder order(int order) {
            this.order = order;
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

        public Builder freeChildPositioning(boolean freeChildPositioning) {
            this.freeChildPositioning = freeChildPositioning;
            return this;
        }

        public Builder childLabelMutation(String childLabelMutation) {
            this.childLabelMutation = childLabelMutation;
            return this;
        }

        public ComponentPropertyCusto build() {
            ComponentPropertyCusto componentPropertyCusto = new ComponentPropertyCusto();
            componentPropertyCusto.order = order;
            componentPropertyCusto.image = image;
            componentPropertyCusto.imagex2 = imagex2;
            componentPropertyCusto.childLabelMutation = childLabelMutation;
            componentPropertyCusto.freeChildPositioning = freeChildPositioning;
            return componentPropertyCusto;
        }
    }
}
