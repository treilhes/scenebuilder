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
package com.oracle.javafx.scenebuilder.core.metadata.property.value;

import com.oracle.javafx.scenebuilder.core.metadata.util.InspectorPath;
import com.oracle.javafx.scenebuilder.core.metadata.util.PropertyName;

import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Region;

/**
 * Base class for double
 */
public abstract class DoublePropertyMetadata extends NumberPropertyMetadata<java.lang.Double> {

    public DoublePropertyMetadata(PropertyName name, boolean readWrite, Double defaultValue, InspectorPath inspectorPath) {
        super(name, Double.class, readWrite, defaultValue, inspectorPath);
        //constants.put("MAX_VALUE", Double.MAX_VALUE); //NOI18N
        setMin(-Double.MAX_VALUE);
        setMax(Double.MAX_VALUE);
    }
    
    public boolean isValidValue(Double value) {
        return true;
    }
    
    public Double getCanonicalValue(Double value) {
        return value;
    }
    
    @Override
    public Double makeValueFromString(String string) {
        return Double.valueOf(string);
    }
    
    /**
     * Accept any double and null (Equivalent to old DoubleKind.NULLABLE_COORDINATE)
     */
    public static class NullableCoordinateDoublePropertyMetadata extends DoublePropertyMetadata {
        public NullableCoordinateDoublePropertyMetadata(PropertyName name, boolean readWrite, Double defaultValue, InspectorPath inspectorPath) {
            super(name, readWrite, defaultValue, inspectorPath);
            constants.put("NULL", null);
        }
        
        @Override
        public boolean isValidValue(Double value) {
            return value != null;
        }
    }

    /**
     * Accept any double with x != null (Equivalent to old DoubleKind.COORDINATE)
     */
    public static class CoordinateDoublePropertyMetadata extends DoublePropertyMetadata {
        public CoordinateDoublePropertyMetadata(PropertyName name, boolean readWrite, Double defaultValue, InspectorPath inspectorPath) {
            super(name, readWrite, defaultValue, inspectorPath);
        }
        
        @Override
        public boolean isValidValue(Double value) {
            return value != null;
        }
    }
    
    /**
     * Accept any double x with x >= 0.0 (Equivalent to old DoubleKind.SIZE)
     */
    public static class SizeDoublePropertyMetadata extends CoordinateDoublePropertyMetadata {
        public SizeDoublePropertyMetadata(PropertyName name, boolean readWrite, Double defaultValue, InspectorPath inspectorPath) {
            super(name, readWrite, defaultValue, inspectorPath);
        }
        
        @Override
        public boolean isValidValue(Double value) {
            return super.isValidValue(value) && 0 <= value;
        }
    }
    
    /**
     * Accept any double x with x >= 0 or x == Region.USE_COMPUTED_SIZE (Equivalent to old DoubleKind.USE_COMPUTED_SIZE)
     */
    public static class ComputedSizeDoublePropertyMetadata extends CoordinateDoublePropertyMetadata {
        public ComputedSizeDoublePropertyMetadata(PropertyName name, boolean readWrite, Double defaultValue, InspectorPath inspectorPath) {
            super(name, readWrite, defaultValue, inspectorPath);
            constants.put("USE_COMPUTED_SIZE", Region.USE_COMPUTED_SIZE);
        }
        
        @Override
        public boolean isValidValue(Double value) {
            return super.isValidValue(value) && ((0 <= value) || (value == Region.USE_COMPUTED_SIZE));
        }
    }

    /**
     * Accept any double x with x >= 0 or x == Region.USE_COMPUTED_SIZE or x == Region.USE_PREF_SIZE (Equivalent to old DoubleKind.USE_PREF_SIZE)
     */
    public static class ComputedAndPrefSizeDoublePropertyMetadata extends CoordinateDoublePropertyMetadata {
        public ComputedAndPrefSizeDoublePropertyMetadata(PropertyName name, boolean readWrite, Double defaultValue, InspectorPath inspectorPath) {
            super(name, readWrite, defaultValue, inspectorPath);
            constants.put("USE_COMPUTED_SIZE", Region.USE_COMPUTED_SIZE);
            constants.put("USE_PREF_SIZE", Region.USE_PREF_SIZE);
        }
        
        @Override
        public boolean isValidValue(Double value) {
            return super.isValidValue(value) && ((0 <= value) || (value == Region.USE_COMPUTED_SIZE) || (value == Region.USE_PREF_SIZE));
        }
    }
    
    /**
     * Accept any double x with 0 <= x <= 255.0 (Equivalent to old DoubleKind.EFFECT_SIZE)
     */
    public static class EffectSizeDoublePropertyMetadata extends CoordinateDoublePropertyMetadata {
        public EffectSizeDoublePropertyMetadata(PropertyName name, boolean readWrite, Double defaultValue, InspectorPath inspectorPath) {
            super(name, readWrite, defaultValue, inspectorPath);
            setMin(0.0);
            setMax(255.0);
        }
        
        @Override
        public boolean isValidValue(Double value) {
            return super.isValidValue(value) && (0 <= value) && (value <= 255.0);
        }
        
        @Override
        public Double getCanonicalValue(Double value) {
            final Double result;
            
            if (value == null) {
                result = null;
            } else {
                result = Math.min(255.0, Math.max(0, value));
            }
            
            return result;
        }
    }
    
    /**
     * Accept any double x with 0 <= x <= 360.0 (Equivalent to old DoubleKind.ANGLE)
     */
    public static class AngleDoublePropertyMetadata extends CoordinateDoublePropertyMetadata {
        public AngleDoublePropertyMetadata(PropertyName name, boolean readWrite, Double defaultValue, InspectorPath inspectorPath) {
            super(name, readWrite, defaultValue, inspectorPath);
        }
        
        @Override
        public boolean isValidValue(Double value) {
            return super.isValidValue(value) && (0 <= value) && (value <= 360.0);
        }
        
        @Override
        public Double getCanonicalValue(Double value) {
            final Double result;
            
            if (value == null) {
                result = null;
            } else {
                result = Math.IEEEremainder(value, 360.0);
            }
            
            return result;
        }
    }
    
    /**
     * Accept any double x with 0 <= x <= 1.0 (Equivalent to old DoubleKind.OPACITY)
     */
    public static class OpacityDoublePropertyMetadata extends CoordinateDoublePropertyMetadata {
        public OpacityDoublePropertyMetadata(PropertyName name, boolean readWrite, Double defaultValue, InspectorPath inspectorPath) {
            super(name, readWrite, defaultValue, inspectorPath);
            setMin(0.0);
            setMax(1.0);
        }
        
        @Override
        public boolean isValidValue(Double value) {
            return super.isValidValue(value) && (0 <= value) && (value <= 1.0);
        }
        
        @Override
        public Double getCanonicalValue(Double value) {
            final Double result;
            
            if (value == null) {
                result = null;
            } else {
                result = Math.min(1, Math.max(0, value));
            }
            
            return result;
        }
    }
    
    /**
     * Accept any double x with 0 <= x <= 1.0 (Equivalent to old DoubleKind.PROGRESS)
     */
    public static class ProgressDoublePropertyMetadata extends CoordinateDoublePropertyMetadata {
        public ProgressDoublePropertyMetadata(PropertyName name, boolean readWrite, Double defaultValue, InspectorPath inspectorPath) {
            super(name, readWrite, defaultValue, inspectorPath);
            constants.put("INDETERMINATE", ProgressIndicator.INDETERMINATE_PROGRESS);
            setMin(0.0);
            setMax(1.0);
        }
        
        @Override
        public boolean isValidValue(Double value) {
            return super.isValidValue(value) && (0 <= value) && (value <= 1.0);
        }
        
        @Override
        public Double getCanonicalValue(Double value) {
            final Double result;
            
            if (value == null) {
                result = null;
            } else {
                result = Math.min(1, Math.max(0, value));
            }
            
            return result;
        }
    }
    
    /**
     * Accept any double x with x == -1 or 0 <= x <= 100.0 (Equivalent to old DoubleKind.PERCENTAGE)
     */
    public static class PercentageDoublePropertyMetadata extends CoordinateDoublePropertyMetadata {
        public PercentageDoublePropertyMetadata(PropertyName name, boolean readWrite, Double defaultValue, InspectorPath inspectorPath) {
            super(name, readWrite, defaultValue, inspectorPath);
            setMin(0.0);
            setMax(100.0);
        }
        
        @Override
        public boolean isValidValue(Double value) {
            return super.isValidValue(value) && ((value == -1) || (0 <= value) && (value <= 100.0));
        }
    }

}
