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
package com.gluonhq.jfxapps.core.metadata.property.value;

import java.util.Map;

import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Region;

/**
 * Base class for double
 */
public abstract class DoublePropertyMetadata<VC> extends NumberPropertyMetadata<java.lang.Double, VC> {

//    public DoublePropertyMetadata(PropertyName name, boolean readWrite, Double defaultValue, InspectorPath inspectorPath) {
//        super(name, Double.class, readWrite, defaultValue, inspectorPath);
//        //constants.put("MAX_VALUE", Double.MAX_VALUE); //NOCHECK
//        this.(-Double.MAX_VALUE);
//        setMax(Double.MAX_VALUE);
//    }
    protected DoublePropertyMetadata(AbstractBuilder<?, ?, VC> builder) {
        super(builder);
        //constants.put("MAX_VALUE", Double.MAX_VALUE); //NOCHECK
        this.setMin(builder.min == null ? -Double.MAX_VALUE : builder.min);
        this.setMax(builder.max == null ? Double.MAX_VALUE : builder.max);
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

    protected static abstract class AbstractBuilder<SELF, TOBUILD, VC> extends NumberPropertyMetadata.AbstractBuilder<SELF, TOBUILD, Double, VC> {

        public AbstractBuilder() {
            super();
            valueClass(Double.class);
        }

    }
    /**
     * Accept any double and null (Equivalent to old DoubleKind.NULLABLE_COORDINATE)
     */
    public static class NullableCoordinateDoublePropertyMetadata<VC> extends DoublePropertyMetadata<VC> {
//        public NullableCoordinateDoublePropertyMetadata(PropertyName name, boolean readWrite, Double defaultValue, InspectorPath inspectorPath) {
//            super(name, readWrite, defaultValue, inspectorPath);
//            constants.put("NULL", null);
//        }

        protected NullableCoordinateDoublePropertyMetadata(AbstractBuilder<?, ?, VC> builder) {
            super(builder);
        }

        @Override
        public boolean isValidValue(Double value) {
            return true;
        }

        protected static abstract class AbstractBuilder<SELF, TOBUILD, VC> extends DoublePropertyMetadata.AbstractBuilder<SELF, TOBUILD, VC> {
            public AbstractBuilder() {
                super();
                constant("NULL", null);
            }
        }

        public static final class Builder<VC> extends AbstractBuilder<Builder<VC>, NullableCoordinateDoublePropertyMetadata<VC>, VC> {
            @Override
            public NullableCoordinateDoublePropertyMetadata<VC> build() {
                return new NullableCoordinateDoublePropertyMetadata<VC>(this);
            }
        }
    }

    /**
     * Accept any double with x != null (Equivalent to old DoubleKind.COORDINATE)
     */
    public static class CoordinateDoublePropertyMetadata<VC> extends DoublePropertyMetadata<VC> {
//        public CoordinateDoublePropertyMetadata(PropertyName name, boolean readWrite, Double defaultValue, InspectorPath inspectorPath) {
//            super(name, readWrite, defaultValue, inspectorPath);
//        }

        protected CoordinateDoublePropertyMetadata(AbstractBuilder<?, ?, VC> builder) {
            super(builder);
        }

        @Override
        public boolean isValidValue(Double value) {
            return value != null;
        }

        public static final class Builder<VC> extends AbstractBuilder<Builder<VC>, CoordinateDoublePropertyMetadata<VC>, VC> {
            @Override
            public CoordinateDoublePropertyMetadata<VC> build() {
                return new CoordinateDoublePropertyMetadata<VC>(this);
            }
        }
    }

    /**
     * Accept any double x with x >= 0.0 (Equivalent to old DoubleKind.SIZE)
     */
    public static class SizeDoublePropertyMetadata<VC> extends CoordinateDoublePropertyMetadata<VC> {
//        public SizeDoublePropertyMetadata(PropertyName name, boolean readWrite, Double defaultValue, InspectorPath inspectorPath) {
//            super(name, readWrite, defaultValue, inspectorPath);
//        }
        protected SizeDoublePropertyMetadata(AbstractBuilder<?, ?, VC> builder) {
            super(builder);
        }

        @Override
        public boolean isValidValue(Double value) {
            return super.isValidValue(value) && 0 <= value;
        }

        protected static abstract class AbstractBuilder<SELF, TOBUILD, VC> extends CoordinateDoublePropertyMetadata.AbstractBuilder<SELF, TOBUILD, VC> {

        }

        public static final class Builder<VC> extends AbstractBuilder<Builder<VC>, SizeDoublePropertyMetadata<VC>, VC> {
            @Override
            public SizeDoublePropertyMetadata<VC> build() {
                return new SizeDoublePropertyMetadata<VC>(this);
            }
        }
    }

    /**
     * Accept any double x with x >= 0 or x == Region.USE_COMPUTED_SIZE (Equivalent to old DoubleKind.USE_COMPUTED_SIZE)
     */
    public static class ComputedSizeDoublePropertyMetadata<VC> extends CoordinateDoublePropertyMetadata<VC> {
//        public ComputedSizeDoublePropertyMetadata(PropertyName name, boolean readWrite, Double defaultValue, InspectorPath inspectorPath) {
//            super(name, readWrite, defaultValue, inspectorPath);
//            constants.put("USE_COMPUTED_SIZE", Region.USE_COMPUTED_SIZE);
//        }

        protected ComputedSizeDoublePropertyMetadata(AbstractBuilder<?, ?, VC> builder) {
            super(builder);
        }

        @Override
        public boolean isValidValue(Double value) {
            return super.isValidValue(value) && ((0 <= value) || (value == Region.USE_COMPUTED_SIZE));
        }

        protected static abstract class AbstractBuilder<SELF, TOBUILD, VC> extends CoordinateDoublePropertyMetadata.AbstractBuilder<SELF, TOBUILD, VC> {
            public AbstractBuilder() {
                super();
                constant("USE_COMPUTED_SIZE", Region.USE_COMPUTED_SIZE);
            }
        }

        public static final class Builder<VC> extends AbstractBuilder<Builder<VC>, ComputedSizeDoublePropertyMetadata<VC>, VC> {
            @Override
            public ComputedSizeDoublePropertyMetadata<VC> build() {
                return new ComputedSizeDoublePropertyMetadata<VC>(this);
            }
        }
    }

    /**
     * Accept any double x with x >= 0 or x == Region.USE_COMPUTED_SIZE or x == Region.USE_PREF_SIZE (Equivalent to old DoubleKind.USE_PREF_SIZE)
     */
    public static class ComputedAndPrefSizeDoublePropertyMetadata<VC> extends CoordinateDoublePropertyMetadata<VC> {
//        public ComputedAndPrefSizeDoublePropertyMetadata(PropertyName name, boolean readWrite, Double defaultValue, InspectorPath inspectorPath) {
//            super(name, readWrite, defaultValue, inspectorPath);
//            constants.put("USE_COMPUTED_SIZE", Region.USE_COMPUTED_SIZE);
//            constants.put("USE_PREF_SIZE", Region.USE_PREF_SIZE);
//        }
        protected ComputedAndPrefSizeDoublePropertyMetadata(AbstractBuilder<?, ?, VC> builder) {
            super(builder);
        }

        @Override
        public boolean isValidValue(Double value) {
            return super.isValidValue(value) && ((0 <= value) || (value == Region.USE_COMPUTED_SIZE) || (value == Region.USE_PREF_SIZE));
        }

        protected static abstract class AbstractBuilder<SELF, TOBUILD, VC> extends CoordinateDoublePropertyMetadata.AbstractBuilder<SELF, TOBUILD, VC> {
            public AbstractBuilder() {
                super();
                constant("USE_COMPUTED_SIZE", Region.USE_COMPUTED_SIZE);
                constant("USE_PREF_SIZE", Region.USE_PREF_SIZE);
            }
        }

        public static final class Builder<VC> extends AbstractBuilder<Builder<VC>, ComputedAndPrefSizeDoublePropertyMetadata<VC>, VC> {
            @Override
            public ComputedAndPrefSizeDoublePropertyMetadata<VC> build() {
                return new ComputedAndPrefSizeDoublePropertyMetadata<VC>(this);
            }
        }
    }

    /**
     * Accept any double x with 0 &lt;= x &lt;= 255.0 (Equivalent to old DoubleKind.EFFECT_SIZE)
     */
    public static class EffectSizeDoublePropertyMetadata<VC> extends CoordinateDoublePropertyMetadata<VC> {
//        public EffectSizeDoublePropertyMetadata(PropertyName name, boolean readWrite, Double defaultValue, InspectorPath inspectorPath) {
//            super(name, readWrite, defaultValue, inspectorPath);
//            setMin(0.0);
//            setMax(255.0);
//        }

        protected EffectSizeDoublePropertyMetadata(AbstractBuilder<?, ?, VC> builder) {
            super(builder);
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

        protected static abstract class AbstractBuilder<SELF, TOBUILD, VC> extends CoordinateDoublePropertyMetadata.AbstractBuilder<SELF, TOBUILD, VC> {
            public AbstractBuilder() {
                super();
                min(0.0);
                max(255.0);
            }
        }

        public static final class Builder<VC> extends AbstractBuilder<Builder<VC>, EffectSizeDoublePropertyMetadata<VC>, VC> {
            @Override
            public EffectSizeDoublePropertyMetadata<VC> build() {
                return new EffectSizeDoublePropertyMetadata<VC>(this);
            }
        }
    }

    /**
     * Accept any double x with 0 &lt;= x &lt;= 360.0 (Equivalent to old DoubleKind.ANGLE)
     */
    public static class AngleDoublePropertyMetadata<VC> extends CoordinateDoublePropertyMetadata<VC> {
//        public AngleDoublePropertyMetadata(PropertyName name, boolean readWrite, Double defaultValue, InspectorPath inspectorPath) {
//            super(name, readWrite, defaultValue, inspectorPath);
//        }

        protected AngleDoublePropertyMetadata(AbstractBuilder<?, ?, VC> builder) {
            super(builder);
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

        protected static abstract class AbstractBuilder<SELF, TOBUILD, VC> extends CoordinateDoublePropertyMetadata.AbstractBuilder<SELF, TOBUILD, VC> {
        }

        public static final class Builder<VC> extends AbstractBuilder<Builder<VC>, AngleDoublePropertyMetadata<VC>, VC> {
            @Override
            public AngleDoublePropertyMetadata<VC> build() {
                return new AngleDoublePropertyMetadata<VC>(this);
            }
        }
    }

    /**
     * Accept any double x with 0 &lt;= x &lt;= 1.0 (Equivalent to old DoubleKind.OPACITY)
     */
    public static class OpacityDoublePropertyMetadata<VC> extends CoordinateDoublePropertyMetadata<VC> {
//        public OpacityDoublePropertyMetadata(PropertyName name, boolean readWrite, Double defaultValue, InspectorPath inspectorPath) {
//            super(name, readWrite, defaultValue, inspectorPath);
//            setMin(0.0);
//            setMax(1.0);
//        }

        protected OpacityDoublePropertyMetadata(AbstractBuilder<?, ?, VC> builder) {
            super(builder);
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

        protected static abstract class AbstractBuilder<SELF, TOBUILD, VC> extends CoordinateDoublePropertyMetadata.AbstractBuilder<SELF, TOBUILD, VC> {
            public AbstractBuilder() {
                super();
                min(0.0);
                max(1.0);
            }
        }

        public static final class Builder<VC> extends AbstractBuilder<Builder<VC>, OpacityDoublePropertyMetadata<VC>, VC> {
            @Override
            public OpacityDoublePropertyMetadata<VC> build() {
                return new OpacityDoublePropertyMetadata<VC>(this);
            }
        }
    }

    /**
     * Accept any double x with 0 &lt;= x &lt;= 1.0 (Equivalent to old DoubleKind.PROGRESS)
     */
    public static class ProgressDoublePropertyMetadata<VC> extends CoordinateDoublePropertyMetadata<VC> {
//        public ProgressDoublePropertyMetadata(PropertyName name, boolean readWrite, Double defaultValue, InspectorPath inspectorPath) {
//            super(name, readWrite, defaultValue, inspectorPath);
//            constants.put("INDETERMINATE", ProgressIndicator.INDETERMINATE_PROGRESS);
//            setMin(0.0);
//            setMax(1.0);
//        }

        protected ProgressDoublePropertyMetadata(AbstractBuilder<?, ?, VC> builder) {
            super(builder);
        }

        @Override
        public boolean isValidValue(Double value) {
            return super.isValidValue(value)
                    && (((0 <= value) && (value <= 1.0)) || (value == ProgressIndicator.INDETERMINATE_PROGRESS));
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

        protected static abstract class AbstractBuilder<SELF, TOBUILD, VC> extends CoordinateDoublePropertyMetadata.AbstractBuilder<SELF, TOBUILD, VC> {
            public AbstractBuilder() {
                super();
                constant("INDETERMINATE", ProgressIndicator.INDETERMINATE_PROGRESS);
                min(0.0);
                max(1.0);
            }
        }

        public static final class Builder<VC> extends AbstractBuilder<Builder<VC>, ProgressDoublePropertyMetadata<VC>, VC> {
            @Override
            public ProgressDoublePropertyMetadata<VC> build() {
                return new ProgressDoublePropertyMetadata<VC>(this);
            }
        }
    }

    /**
     * Accept any double x with x == -1 or 0 &lt;= x &lt;= 100.0 (Equivalent to old DoubleKind.PERCENTAGE)
     */
    public static class PercentageDoublePropertyMetadata<VC> extends CoordinateDoublePropertyMetadata<VC> {
//        public PercentageDoublePropertyMetadata(PropertyName name, boolean readWrite, Double defaultValue, InspectorPath inspectorPath) {
//            super(name, readWrite, defaultValue, inspectorPath);
//            setMin(0.0);
//            setMax(100.0);
//        }

        protected PercentageDoublePropertyMetadata(AbstractBuilder<?, ?, VC> builder) {
            super(builder);
        }

        @Override
        public boolean isValidValue(Double value) {
            return super.isValidValue(value) && ((value == -1) || (0 <= value) && (value <= 100.0));
        }

        protected static abstract class AbstractBuilder<SELF, TOBUILD, VC> extends CoordinateDoublePropertyMetadata.AbstractBuilder<SELF, TOBUILD, VC> {
            public AbstractBuilder() {
                super();
                min(0.0);
                max(100.0);
            }
        }

        public static final class Builder<VC> extends AbstractBuilder<Builder<VC>, PercentageDoublePropertyMetadata<VC>, VC> {
            @Override
            public PercentageDoublePropertyMetadata<VC> build() {
                return new PercentageDoublePropertyMetadata<VC>(this);
            }
        }
    }

    public static class CustomizableDoublePropertyMetadata<VC> extends CoordinateDoublePropertyMetadata<VC> {

      protected CustomizableDoublePropertyMetadata(AbstractBuilder<?, ?, VC> builder) {
          super(builder);
      }

      protected static abstract class AbstractBuilder<SELF, TOBUILD, VC> extends CoordinateDoublePropertyMetadata.AbstractBuilder<SELF, TOBUILD, VC> {

        @Override
        public SELF min(Double min) {
            return super.min(min);
        }

        @Override
        public SELF max(Double max) {
            return super.max(max);
        }

        @Override
        public SELF lenientBoundary(boolean lenientBoundary) {
            return super.lenientBoundary(lenientBoundary);
        }

        @Override
        public SELF defaultValue(Double defaultValue) {
            return super.defaultValue(defaultValue);
        }

        @Override
        public SELF constant(String constantName, Object constantValue) {
            return super.constant(constantName, constantValue);
        }

        @Override
        public SELF constants(Map<String, Object> constants) {
            return super.constants(constants);
        }

      }

      public static final class Builder<VC> extends AbstractBuilder<Builder<VC>, CustomizableDoublePropertyMetadata<VC>, VC> {
          @Override
          public CustomizableDoublePropertyMetadata<VC> build() {
              return new CustomizableDoublePropertyMetadata<VC>(this);
          }
      }
  }
}
