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
        public final static String ID = "ctxEditMenu";
        public final static String CUT_ID = ID + "-cut";
        public final static String COPY_ID = ID + "-copy";
        public final static String PASTE_ID = ID + "-paste";
        public final static String PASTE_INTO_ID = ID + "-pasteinto";
        public final static String DUPLICATE_ID = ID + "-duplicate";
        public final static String DELETE_ID = ID + "-delete";
    }

    public interface Modify {
        public final static String ID = "ctxModifyMenu";
        public final static String FIT_TO_PARENT_ID = ID + "-fittoparent";
        public final static String USE_COMPUTED_SIZE_ID = ID + "-usecomputedsize";
    }

    public interface File {
        public final static String ID = "ctxFileMenu";
        public final static String EDIT_INCLUDED_ID = ID + "-editincluded";
        public final static String REVEAL_INCLUDED_ID = ID + "-revealincluded";
    }

    public interface Arrange {
        public final static String ID = "ctxArrangeMenu";
        public final static String BRING_TO_FRONT_ID = ID + "-bringtofront";
        public final static String SEND_TO_BACK_ID = ID + "-sendtoback";
        public final static String BRING_FORWARD_ID = ID + "-bringforward";
        public final static String SEND_BACKWARD_ID = ID + "-sendbackward";
        public final static String WRAP_ID = ID + "-wrap";
        public final static String UNWRAP_ID = ID + "-unwrap";
    }

}
