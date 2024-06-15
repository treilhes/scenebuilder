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
package com.gluonhq.jfxapps.core.api.mask;

import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.gluonhq.jfxapps.core.metadata.klass.ComponentClassMetadata;
import com.gluonhq.jfxapps.core.metadata.property.ComponentPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.PropertyMetadata.Visibility;

public class AccessoryGeneric<CCM extends ComponentClassMetadata,CPM extends ComponentPropertyMetadata> implements Accessory<CPM>{

    private final CCM owner;
    private final CPM propertyMetadata;

    public AccessoryGeneric(CCM owner, CPM propertyMetadata) {
        super();
        this.owner = owner;
        this.propertyMetadata = propertyMetadata;
    }

    public CCM getOwner() {
        return owner;
    }

    @Override
    public CPM getPropertyMetadata() {
        return propertyMetadata;
    }

    @Override
    public PropertyName getName() {
        return propertyMetadata.getName();
    }

    @Override
    public Class<?> getContentType() {
        if (propertyMetadata != null && propertyMetadata.getClassMetadata() != null) {
            return propertyMetadata.getClassMetadata().getKlass();
        }
        return null;
    }

    @Override
    public boolean isCollection() {
        return propertyMetadata.isCollection();
    }

    @Override
    public boolean isAccepting(Object value) {
        if (value == null) {
            return false;
        }
        return isAccepting(value.getClass());
    }

    @Override
    public boolean isAccepting(Class<?> valueClass) {
        final boolean result;
        if (propertyMetadata == null) {
            result = false;
        } else if (getContentType() == null) {
            result = true;
        } else {
            result = getContentType().isAssignableFrom(valueClass);
        }

        return result;
    }

    @Override
    public boolean isMain() {
        return propertyMetadata.isMain();
    }

    @Override
    public boolean isHidden() {
        return propertyMetadata.getVisibility() == Visibility.HIDDEN;
    }

    @Override
    public boolean isStandard() {
        return propertyMetadata.getVisibility() == Visibility.STANDARD;
    }

    @Override
    public boolean isExpert() {
        return propertyMetadata.getVisibility() == Visibility.EXPERT;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((propertyMetadata == null) ? 0 : propertyMetadata.getName().getName().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AccessoryGeneric other = (AccessoryGeneric) obj;
        if (propertyMetadata == null) {
            if (other.propertyMetadata != null)
                return false;
        } else if (!propertyMetadata.getName().getName().equals(other.propertyMetadata.getName().getName()))
            return false;
        return true;
    }

}