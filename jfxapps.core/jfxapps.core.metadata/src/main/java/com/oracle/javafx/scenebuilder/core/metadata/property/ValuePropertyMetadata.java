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
package com.oracle.javafx.scenebuilder.core.metadata.property;

import java.util.HashMap;
import java.util.Map;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMElement;
import com.oracle.javafx.scenebuilder.core.metadata.util.InspectorPath;

/**
 * This class describes a single valued property
 *
 */
public abstract class ValuePropertyMetadata extends PropertyMetadata {

    /** Is property writable. */
    private final boolean readWrite;

    //TODO this property must be extracted elsewhere in the future "Inspector extension" or better if possible
    /** The inspector path. */
    private final InspectorPath inspectorPath;

    /** The default value alternatives. */
    private final Map<Class<?>, Object> defaultValueAlternatives = new HashMap<>();

//    /**
//     * Instantiates a new value property metadata.
//     *
//     * @param name the property name
//     * @param readWrite Is the property writable?
//     * @param inspectorPath the inspector path
//     */
//    public ValuePropertyMetadata(PropertyName name, boolean readWrite, InspectorPath inspectorPath) {
//        this(name, false, readWrite, inspectorPath);
//    }
//
//    /**
//     * Instantiates a new value property metadata.
//     *
//     * @param name the property name
//     * @param readWrite Is the property writable?
//     * @param inspectorPath the inspector path
//     */
//    protected ValuePropertyMetadata(PropertyName name, boolean isGroup, boolean readWrite, InspectorPath inspectorPath) {
//        super(name, isGroup);
//        this.readWrite = readWrite;
//        this.inspectorPath = inspectorPath;
//    }

    protected ValuePropertyMetadata(AbstractBuilder<?, ?> builder) {
        super(builder);
        this.readWrite = builder.readWrite;
        this.inspectorPath = builder.inspectorPath;
        this.defaultValueAlternatives.putAll(builder.defaultValueAlternatives);
    }

    /**
     * Checks if the property is writable.
     *
     * @return true, if the property is writable
     */
    public boolean isReadWrite() {
        return readWrite;
    }


    /**
     * Gets the inspector path.
     *
     * @return the inspector path
     */
    public InspectorPath getInspectorPath() {
        return inspectorPath;
    }

    /**
     * Gets the value class.
     *
     * @return the value class
     */
    public abstract Class<?> getValueClass();

    /**
     * Gets the default value object.
     *
     * @return the default value object
     */
    public abstract Object getDefaultValueObject();

    /**
     * Gets the current value object from the given fxom instance.
     *
     * @param fxomInstance the fxom instance
     * @return the value object
     */
    public abstract Object getValueObject(FXOMElement fxomInstance);

    /**
     * Sets the current value object from the given fxom instance
     *
     * @param fxomInstance the fxom instance
     * @param valueObject the value object
     */
    public abstract void setValueObject(FXOMElement fxomInstance, Object valueObject);

    public Map<Class<?>, Object> getDefaultValueAlternatives() {
        return defaultValueAlternatives;
    }


    /**
     * Returns true if getName().getResidenceClass() != null.
     * @return true if getName().getResidenceClass() != null.
     */
    public boolean isStaticProperty() {
        return getName().getResidenceClass() != null;
    }

    /**
     * Sets the property value in the scene graph object.
     * FXOM instance is unchanged.
     * Value is lost at next scene graph reconstruction.
     *
     * @param fxomInstance an fxom instance (never null)
     * @param value a value conform with the property typing
     */
    public void setValueInSceneGraphObject(FXOMElement fxomInstance, Object value) {
        assert fxomInstance != null;
        assert fxomInstance.getSceneGraphObject() != null;
        getName().setValue(fxomInstance.getSceneGraphObject(), value);
    }

    /**
     * Gets the property value in the scene graph object.<br>
     * Result might be different from getValueObject().<br>
     * For example, if Button.text contains a resource key 'button-key'<br>
     * and a resource bundle assign 'OK' to this key:<br>
     *    - getValueObject() -&gt; '%button-key'<br>
     *    - getValueInSceneGraphObject() -&gt; 'OK'<br>
     *<br>
     * @param fxomInstance an fxom instance (never null)
     * @return value of this property in the scene graph object associated
     *         fxomInstance
     */
    public Object getValueInSceneGraphObject(FXOMElement fxomInstance) {
        assert fxomInstance != null;
        return getName().getValue(fxomInstance.getSceneGraphObject());
    }

    /*
     * Object
     */

    @Override
    public int hashCode() {  // To please FindBugs
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {  // To please FindBugs
        if (obj == null) {
            return false;
        }
        if (PropertyMetadata.class != obj.getClass()) {
            return false;
        }

        return super.equals(obj);
    }

    protected static abstract class AbstractBuilder<SELF, TOBUILD> extends PropertyMetadata.AbstractBuilder<SELF, TOBUILD> {
        /** Is property writable. */
        private boolean readWrite;

        //TODO this property must be extracted elsewhere in the future "Inspector extension" or better if possible
        /** The inspector path. */
        private InspectorPath inspectorPath;

        /** The default value alternatives. */
        private final Map<Class<?>, Object> defaultValueAlternatives = new HashMap<>();

        @Override
        protected SELF withConstant(String constantName, Object constantValue) {
            return super.withConstant(constantName, constantValue);
        }

        public SELF withReadWrite(boolean readWrite) {
            this.readWrite = readWrite;
            return self();
        }

        public SELF withInspectorPath(InspectorPath inspectorPath) {
            this.inspectorPath = inspectorPath;
            return self();
        }

        protected SELF withDefaultAlternativeValue(Class<?> cls, Object value) {
            this.defaultValueAlternatives.put(cls, value);
            return self();
        }

    }

    public static <V extends ValuePropertyMetadata , T extends ValuePropertyMetadata.AbstractBuilder<T, V>> T fillBuilder(ValuePropertyMetadata source, T builder){
        return builder
                .withName(source.getName())
                .withReadWrite(source.isReadWrite())
                .withInspectorPath(source.getInspectorPath());
    }
}
