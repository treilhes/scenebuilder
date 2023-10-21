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
package com.oracle.javafx.scenebuilder.api.om;

import java.util.HashSet;
import java.util.Set;

/**
 * This class groups static utility methods which operate on FXOMNode and
 * subclasses (a bit like Collection and Collections).
 *
 *
 */
public class OMNodes {

    /**
     * Flattens a set of fxom objects.
     * A set of fxom objects is declared "flat" if each object member
     * of the set has no ancestor member of the set.
     *
     * @param objects a set of fxom objects (never null)
     * @return the flat set of objects.
     */
    public static <T extends OMObject> Set<T> flatten(Set<T> objects) {
        final Set<T> result = new HashSet<>();

        assert objects != null;

        for (T o : objects) {
            if (lookupAncestor(o, objects) == null) {
                result.add(o);
            }
        }

        return result;
    }


    /**
     * Returns null or the first ancestor of "obj" which belongs to "candidates".
     * @param obj an fxom object (never null)
     * @param candidates a set of fxom object (not null and not empty)
     * @return null or the first ancestor of "obj" which belongs to "candidates".
     */
    public static <T extends OMObject> T lookupAncestor(T obj, Set<T> candidates) {
        assert obj != null;
        assert candidates != null;
        assert candidates.isEmpty() == false;

        OMObject result = obj.getParentObject();
        while ((result != null) && (candidates.contains(result) == false)) {
            result = result.getParentObject();
        }

        return (T)result;
    }

}
