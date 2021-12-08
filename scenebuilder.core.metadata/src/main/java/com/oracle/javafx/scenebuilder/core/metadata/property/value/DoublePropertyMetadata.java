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

import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Region;

/**
 * Base class for double
 */
public abstract class DoublePropertyMetadata extends NumberPropertyMetadata<java.lang.Double> {

//    public DoublePropertyMetadata(PropertyName name, boolean readWrite, Double defaultValue, InspectorPath inspectorPath) {
//        super(name, Double.class, readWrite, defaultValue, inspectorPath);
//        //constants.put("MAX_VALUE", Double.MAX_VALUE); //NOCHECK
//        this.(-Double.MAX_VALUE);
//        setMax(Double.MAX_VALUE);
//    }
    protected DoublePropertyMetadata(AbstractBuilder<?, ?> builder) {
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

    protected static abstract class AbstractBuilder<SELF, TOBUILD> extends NumberPropertyMetadata.AbstractBuilder<SELF, TOBUILD, Double> {

        public AbstractBuilder() {
            super();
            withValueClass(Double.class);
        }

    }
    /**
     * Accept any double and null (Equivalent to old DoubleKind.NULLABLE_COORDINATE)
     */
    public static class NullableCoordinateDoublePropertyMetadata extends DoublePropertyMetadata {
//        public NullableCoordinateDoublePropertyMetadata(PropertyName name, boolean readWrite, Double defaultValue, InspectorPath inspectorPath) {
//            super(name, readWrite, defaultValue, inspectorPath);
//            constants.put("NULL", null);
//        }

        protected NullableCoordinateDoublePropertyMetadata(AbstractBuilder<?, ?> builder) {
            super(builder);
        }

        @Override
        public boolean isValidValue(Double value) {
            return true;
        }

        protected static abstract class AbstractBuilder<SELF, TOBUILD> extends DoublePropertyMetadata.AbstractBuilder<SELF, TOBUILD> {
            public AbstractBuilder() {
                super();
                withConstant("NULL", null);
            }
        }

        public static final class Builder extends AbstractBuilder<Builder, NullableCoordinateDoublePropertyMetadata> {
            @Override
            public NullableCoordinateDoublePropertyMetadata build() {
                return new NullableCoordinateDoublePropertyMetadata(this);
            }
        }
    }

    /**
     * Accept any double with x != null (Equivalent to old DoubleKind.COORDINATE)
     */
    public static class CoordinateDoublePropertyMetadata extends DoublePropertyMetadata {
//        public CoordinateDoublePropertyMetadata(PropertyName name, boolean readWrite, Double defaultValue, InspectorPath inspectorPath) {
//            super(name, readWrite, defaultValue, inspectorPath);
//        }

        protected CoordinateDoublePropertyMetadata(AbstractBuilder<?, ?> builder) {
            super(builder);
        }

        @Override
        public boolean isValidValue(Double value) {
            return value != null;
        }

        public static final class Builder extends AbstractBuilder<Builder, CoordinateDoublePropertyMetadata> {
            @Override
            public CoordinateDoublePropertyMetadata build() {
                return new CoordinateDoublePropertyMetadata(this);
            }
        }
    }

    /**
     * Accept any double x with x >= 0.0 (Equivalent to old DoubleKind.SIZE)
     */
    public static class SizeDoublePropertyMetadata extends CoordinateDoublePropertyMetadata {
//        public SizeDoublePropertyMetadata(PropertyName name, boolean readWrite, Double defaultValue, InspectorPath inspectorPath) {
//            super(name, readWrite, defaultValue, inspectorPath);
//        }
        protected SizeDoublePropertyMetadata(AbstractBuilder<?, ?> builder) {
            super(builder);
        }

        @Override
        public boolean isValidValue(Double value) {
            return super.isValidValue(value) && 0 <= value;
        }

        protected static abstract class AbstractBuilder<SELF, TOBUILD> extends CoordinateDoublePropertyMetadata.AbstractBuilder<SELF, TOBUILD> {

        }

        public static final class Builder extends AbstractBuilder<Builder, SizeDoublePropertyMetadata> {
            @Override
            public SizeDoublePropertyMetadata build() {
                return new SizeDoublePropertyMetadata(this);
            }
        }
    }

    /**
     * Accept any double x with x >= 0 or x == Region.USE_COMPUTED_SIZE (Equivalent to old DoubleKind.USE_COMPUTED_SIZE)
     */
    public static class ComputedSizeDoublePropertyMetadata extends CoordinateDoublePropertyMetadata {
//        public ComputedSizeDoublePropertyMetadata(PropertyName name, boolean readWrite, Double defaultValue, InspectorPath inspectorPath) {
//            super(name, readWrite, defaultValue, inspectorPath);
//            constants.put("USE_COMPUTED_SIZE", Region.USE_COMPUTED_SIZE);
//        }

        protected ComputedSizeDoublePropertyMetadata(AbstractBuilder<?, ?> builder) {
            super(builder);
        }

        @Override
        public boolean isValidValue(Double value) {
            return super.isValidValue(value) && ((0 <= value) || (value == Region.USE_COMPUTED_SIZE));
        }

        protected static abstract class AbstractBuilder<SELF, TOBUILD> extends CoordinateDoublePropertyMetadata.AbstractBuilder<SELF, TOBUILD> {
            public AbstractBuilder() {
                super();
                withConstant("USE_COMPUTED_SIZE", Region.USE_COMPUTED_SIZE);
            }
        }

        public static final class Builder extends AbstractBuilder<Builder, ComputedSizeDoublePropertyMetadata> {
            @Override
            public ComputedSizeDoublePropertyMetadata build() {
                return new ComputedSizeDoublePropertyMetadata(this);
            }
        }
    }

    /**
     * Accept any double x with x >= 0 or x == Region.USE_COMPUTED_SIZE or x == Region.USE_PREF_SIZE (Equivalent to old DoubleKind.USE_PREF_SIZE)
     */
    public static class ComputedAndPrefSizeDoublePropertyMetadata extends CoordinateDoublePropertyMetadata {
//        public ComputedAndPrefSizeDoublePropertyMetadata(PropertyName name, boolean readWrite, Double defaultValue, InspectorPath inspectorPath) {
//            super(name, readWrite, defaultValue, inspectorPath);
//            constants.put("USE_COMPUTED_SIZE", Region.USE_COMPUTED_SIZE);
//            constants.put("USE_PREF_SIZE", Region.USE_PREF_SIZE);
//        }
        protected ComputedAndPrefSizeDoublePropertyMetadata(AbstractBuilder<?, ?> builder) {
            super(builder);
        }

        @Override
        public boolean isValidValue(Double value) {
            return super.isValidValue(value) && ((0 <= value) || (value == Region.USE_COMPUTED_SIZE) || (value == Region.USE_PREF_SIZE));
        }

        protected static abstract class AbstractBuilder<SELF, TOBUILD> extends CoordinateDoublePropertyMetadata.AbstractBuilder<SELF, TOBUILD> {
            public AbstractBuilder() {
                super();
                withConstant("USE_COMPUTED_SIZE", Region.USE_COMPUTED_SIZE);
                withConstant("USE_PREF_SIZE", Region.USE_PREF_SIZE);
            }
        }

        public static final class Builder extends AbstractBuilder<Builder, ComputedAndPrefSizeDoublePropertyMetadata> {
            @Override
            public ComputedAndPrefSizeDoublePropertyMetadata build() {
                return new ComputedAndPrefSizeDoublePropertyMetadata(this);
            }
        }
    }

    /**
     * Accept any double x with 0 &lt;= x &lt;= 255.0 (Equivalent to old DoubleKind.EFFECT_SIZE)
     */
    public static class EffectSizeDoublePropertyMetadata extends CoordinateDoublePropertyMetadata {
//        public EffectSizeDoublePropertyMetadata(PropertyName name, boolean readWrite, Double defaultValue, InspectorPath inspectorPath) {
//            super(name, readWrite, defaultValue, inspectorPath);
//            setMin(0.0);
//            setMax(255.0);
//        }

        protected EffectSizeDoublePropertyMetadata(AbstractBuilder<?, ?> builder) {
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

        protected static abstract class AbstractBuilder<SELF, TOBUILD> extends CoordinateDoublePropertyMetadata.AbstractBuilder<SELF, TOBUILD> {
            public AbstractBuilder() {
                super();
                withMin(0.0);
                withMax(255.0);
            }
        }

        public static final class Builder extends AbstractBuilder<Builder, EffectSizeDoublePropertyMetadata> {
            @Override
            public EffectSizeDoublePropertyMetadata build() {
                return new EffectSizeDoublePropertyMetadata(this);
            }
        }
    }

    /**
     * Accept any double x with 0 &lt;= x &lt;= 360.0 (Equivalent to old DoubleKind.ANGLE)
     */
    public static class AngleDoublePropertyMetadata extends CoordinateDoublePropertyMetadata {
//        public AngleDoublePropertyMetadata(PropertyName name, boolean readWrite, Double defaultValue, InspectorPath inspectorPath) {
//            super(name, readWrite, defaultValue, inspectorPath);
//        }

        protected AngleDoublePropertyMetadata(AbstractBuilder<?, ?> builder) {
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

        protected static abstract class AbstractBuilder<SELF, TOBUILD> extends CoordinateDoublePropertyMetadata.AbstractBuilder<SELF, TOBUILD> {
        }

        public static final class Builder extends AbstractBuilder<Builder, AngleDoublePropertyMetadata> {
            @Override
            public AngleDoublePropertyMetadata build() {
                return new AngleDoublePropertyMetadata(this);
            }
        }
    }

    /**
     * Accept any double x with 0 &lt;= x &lt;= 1.0 (Equivalent to old DoubleKind.OPACITY)
     */
    public static class OpacityDoublePropertyMetadata extends CoordinateDoublePropertyMetadata {
//        public OpacityDoublePropertyMetadata(PropertyName name, boolean readWrite, Double defaultValue, InspectorPath inspectorPath) {
//            super(name, readWrite, defaultValue, inspectorPath);
//            setMin(0.0);
//            setMax(1.0);
//        }

        protected OpacityDoublePropertyMetadata(AbstractBuilder<?, ?> builder) {
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

        protected static abstract class AbstractBuilder<SELF, TOBUILD> extends CoordinateDoublePropertyMetadata.AbstractBuilder<SELF, TOBUILD> {
            public AbstractBuilder() {
                super();
                withMin(0.0);
                withMax(1.0);
            }
        }

        public static final class Builder extends AbstractBuilder<Builder, OpacityDoublePropertyMetadata> {
            @Override
            public OpacityDoublePropertyMetadata build() {
                return new OpacityDoublePropertyMetadata(this);
            }
        }
    }

    /**
     * Accept any double x with 0 &lt;= x &lt;= 1.0 (Equivalent to old DoubleKind.PROGRESS)
     */
    public static class ProgressDoublePropertyMetadata extends CoordinateDoublePropertyMetadata {
//        public ProgressDoublePropertyMetadata(PropertyName name, boolean readWrite, Double defaultValue, InspectorPath inspectorPath) {
//            super(name, readWrite, defaultValue, inspectorPath);
//            constants.put("INDETERMINATE", ProgressIndicator.INDETERMINATE_PROGRESS);
//            setMin(0.0);
//            setMax(1.0);
//        }

        protected ProgressDoublePropertyMetadata(AbstractBuilder<?, ?> builder) {
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

        protected static abstract class AbstractBuilder<SELF, TOBUILD> extends CoordinateDoublePropertyMetadata.AbstractBuilder<SELF, TOBUILD> {
            public AbstractBuilder() {
                super();
                withConstant("INDETERMINATE", ProgressIndicator.INDETERMINATE_PROGRESS);
                withMin(0.0);
                withMax(1.0);
            }
        }

        public static final class Builder extends AbstractBuilder<Builder, ProgressDoublePropertyMetadata> {
            @Override
            public ProgressDoublePropertyMetadata build() {
                return new ProgressDoublePropertyMetadata(this);
            }
        }
    }

    /**
     * Accept any double x with x == -1 or 0 &lt;= x &lt;= 100.0 (Equivalent to old DoubleKind.PERCENTAGE)
     */
    public static class PercentageDoublePropertyMetadata extends CoordinateDoublePropertyMetadata {
//        public PercentageDoublePropertyMetadata(PropertyName name, boolean readWrite, Double defaultValue, InspectorPath inspectorPath) {
//            super(name, readWrite, defaultValue, inspectorPath);
//            setMin(0.0);
//            setMax(100.0);
//        }

        protected PercentageDoublePropertyMetadata(AbstractBuilder<?, ?> builder) {
            super(builder);
        }

        @Override
        public boolean isValidValue(Double value) {
            return super.isValidValue(value) && ((value == -1) || (0 <= value) && (value <= 100.0));
        }

        protected static abstract class AbstractBuilder<SELF, TOBUILD> extends CoordinateDoublePropertyMetadata.AbstractBuilder<SELF, TOBUILD> {
            public AbstractBuilder() {
                super();
                withMin(0.0);
                withMax(100.0);
            }
        }

        public static final class Builder extends AbstractBuilder<Builder, PercentageDoublePropertyMetadata> {
            @Override
            public PercentageDoublePropertyMetadata build() {
                return new PercentageDoublePropertyMetadata(this);
            }
        }
    }

}
