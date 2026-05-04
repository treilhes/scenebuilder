/*
 * Copyright (c) 2016, 2026, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2026, Pascal Treilhes and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.api.menu;

public interface DefaultContextMenu {

    public interface Edit {
        public static final String ID = "ctxEditMenu";
        public static final String CUT_ID = ID + "-cut";
        public static final String COPY_ID = ID + "-copy";
        public static final String PASTE_ID = ID + "-paste";
        public static final String PASTE_INTO_ID = ID + "-pasteinto";
        public static final String DUPLICATE_ID = ID + "-duplicate";
        public static final String DELETE_ID = ID + "-delete";
    }

    public interface Modify {
        public static final String ID = "ctxModifyMenu";
        public static final String FIT_TO_PARENT_ID = ID + "-fittoparent";
        public static final String USE_COMPUTED_SIZE_ID = ID + "-usecomputedsize";
    }

    public interface File {
        public static final String ID = "ctxFileMenu";
        public static final String EDIT_INCLUDED_ID = ID + "-editincluded";
        public static final String REVEAL_INCLUDED_ID = ID + "-revealincluded";
    }

    public interface Arrange {
        public static final String ID = "ctxArrangeMenu";
        public static final String BRING_TO_FRONT_ID = ID + "-bringtofront";
        public static final String SEND_TO_BACK_ID = ID + "-sendtoback";
        public static final String BRING_FORWARD_ID = ID + "-bringforward";
        public static final String SEND_BACKWARD_ID = ID + "-sendbackward";
        public static final String WRAP_ID = ID + "-wrap";
        public static final String UNWRAP_ID = ID + "-unwrap";
    }

}
