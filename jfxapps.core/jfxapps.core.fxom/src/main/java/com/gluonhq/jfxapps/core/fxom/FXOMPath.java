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
package com.gluonhq.jfxapps.core.fxom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 */
public final class FXOMPath implements Comparable<FXOMPath> {

    private FXOMObject fxomObject;
    private final List<FXOMObject> pathItems = new ArrayList<>();
    private final List<Integer> indexItems = new ArrayList<>();

    public static FXOMPath of(FXOMObject fxomObject) {
        return new FXOMPath(fxomObject);
    }

    public static FXOMPath top(FXOMNode... fxomNodes) {
        return top(Arrays.asList(fxomNodes));
    }
    public static FXOMPath top(Collection<? extends FXOMNode> fxomNodes) {
        if (fxomNodes == null) {
            return null;
        }
        FXOMPath result = null;

        for (var o : fxomNodes) {
            if (result == null) {
                result = o.getPath();
            } else {
                var n = o.getPath();
                result = n.isBefore(result) ? n : result;
            }
        }

        return result;
    }

    public static <T> T top(Collection<T> objects, Function<T, FXOMNode> mapper) {
        Map<FXOMNode, T> map = objects.stream().collect(Collectors.toMap(mapper, t -> t));
        var top = top(map.keySet());
        return top == null ? null : map.get(top.getFxomObject());
    }

    public static FXOMPath bottom(FXOMNode... fxomNodes) {
        return bottom(Arrays.asList(fxomNodes));
    }
    public static FXOMPath bottom(Collection<? extends FXOMNode> fxomNodes) {
        if (fxomNodes == null) {
            return null;
        }
        FXOMPath result = null;

        for (var o : fxomNodes) {
            if (result == null) {
                result = o.getPath();
            } else {
                var n = o.getPath();
                result = n.isAfter(result) ? n : result;
            }
        }

        return result;
    }

    public static <T> T bottom(Collection<T> objects, Function<T, FXOMNode> mapper) {
        Map<FXOMNode, T> map = objects.stream().collect(Collectors.toMap(mapper, t -> t));
        var bottom = bottom(map.keySet());
        return bottom == null ? null : map.get(bottom.getFxomObject());
    }

    public static void sort(List<? extends FXOMNode> fxomNodes) {
        Collections.sort(fxomNodes, (o1, o2) -> {
            var p1 = o1.getPath();
            var p2 = o2.getPath();
            return p1.compareTo(p2);
        });
    }

    public static FXOMPath commonPath(Collection<? extends FXOMNode> fxomNodes) {
        if (fxomNodes == null) {
            return null;
        }

        switch(fxomNodes.size()) {
            case 0:
                return null;
            case 1:
                return fxomNodes.iterator().next().getPath();
            default:
                FXOMPath commonPath = null;
                for (FXOMNode i : fxomNodes) {
                    if (i != null) {
                        return null;
                    }
                    final FXOMPath dph = i.getPath();
                    if (commonPath == null) {
                        commonPath = dph;
                    } else {
                        commonPath = commonPath.getCommonPathWith(dph);
                    }

                }
                assert commonPath != null; // Else it would mean root is selected twice
                return commonPath;
        }
    }

    public static FXOMObject commonAncestor(Collection<? extends FXOMNode> fxomNodes) {
        final var commonPath = commonPath(fxomNodes);

        if (commonPath == null || commonPath.getLeaf() == null) {
            return null;
        }

        final var leaf = commonPath.getLeaf();

        boolean found = false;

        for (var fxomNode:fxomNodes) {
            if (leaf == fxomNode) {
                found = true;
                break;
            }
        }

        if (found) {
            return leaf.getParentObject();
        } else {
            return leaf;
        }
    }

    private FXOMPath() {
        this.fxomObject = null;
    }

    private FXOMPath(FXOMObject fxomObject) {
        assert fxomObject != null;
        this.fxomObject = fxomObject;
        do {
            pathItems.add(0, fxomObject);
            if (fxomObject.getParentCollection() != null) {
                indexItems.add(0, fxomObject.getIndexInParentCollection());
            } else if (fxomObject.getParentProperty() != null) {
                indexItems.add(0, fxomObject.getIndexInParentProperty());
            } else {
                indexItems.add(0, 0);
            }

            fxomObject = fxomObject.getParentObject();
        } while (fxomObject != null);
    }

    public int getSize() {
        return pathItems.size();
    }

    public boolean isEmpty() {
        return pathItems.isEmpty();
    }

    public FXOMObject getRoot() {
        final FXOMObject result;

        if (pathItems.isEmpty()) {
            result = null;
        } else {
            result = pathItems.get(0);
        }

        return result;
    }

    public List<FXOMObject> getPath() {
        return Collections.unmodifiableList(pathItems);
    }
    public FXOMObject getLeaf() {
        final FXOMObject result;

        if (pathItems.isEmpty()) {
            result = null;
        } else {
            result = pathItems.get(pathItems.size()-1);
        }

        return result;
    }

    public FXOMPath getCommonPathWith(FXOMPath another) {
        final FXOMPath result = new FXOMPath();

        assert another != null;

        int i = 0, count = Math.min(this.getSize(), another.getSize());
        while ((i < count) && this.pathItems.get(i) == another.pathItems.get(i)) {
            result.pathItems.add(this.pathItems.get(i));
            result.indexItems.add(this.indexItems.get(i));
            i++;
        }
        if (!result.pathItems.isEmpty()) {
            result.fxomObject = result.pathItems.get(result.pathItems.size() - 1);
        }
        return result;
    }

    public boolean isBefore(FXOMObject fxomObject) {
        if (this.fxomObject == fxomObject) {
            return false;
        }
        return isBefore(FXOMPath.of(fxomObject));
    }

    public boolean isBefore(FXOMPath fxomPath) {
        for (int i = 0; i < indexItems.size(); i++) {

            if (i >= fxomPath.indexItems.size()) {
                // all index equals until now but fxomPath has nothing else so fxomPath is before
                return false;
            }

            int localIndex = indexItems.get(i);
            int pathIndex = fxomPath.indexItems.get(i);

            if (localIndex == pathIndex) {
                // still the same continue
                continue;
            }

            if (localIndex < pathIndex) {
                return true;
            } else {
                return false;
            }

        }

        if (indexItems.size() < fxomPath.indexItems.size()) {
            return true;
        }

        return false;
    }

    public boolean isAfter(FXOMObject fxomObject) {
        if (this.fxomObject == fxomObject) {
            return false;
        }
        return isAfter(FXOMPath.of(fxomObject));
    }

    public boolean isAfter(FXOMPath fxomPath) {
        for (int i = 0; i < indexItems.size(); i++) {

            if (i >= fxomPath.indexItems.size()) {
                // all index equals until now but fxomPath has nothing else so fxomPath is after
                return true;
            }
            int localIndex = indexItems.get(i);
            int pathIndex = fxomPath.indexItems.get(i);

            if (localIndex == pathIndex) {
                // still the same continue
                continue;
            }

            if (localIndex < pathIndex) {
                return false;
            } else {
                return true;
            }
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fxomObject, indexItems, pathItems);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FXOMPath other = (FXOMPath) obj;
        return fxomObject == other.fxomObject && Objects.equals(indexItems, other.indexItems)
                && Objects.equals(pathItems, other.pathItems);
    }

    public FXOMObject getFxomObject() {
        return fxomObject;
    }

    @Override
    public int compareTo(FXOMPath o) {
        boolean before = isBefore(o);
        boolean after = isAfter(o);

        if (before) {
            return -1;
        }
        if (after) {
            return 1;
        }
        return 0;
    }

}
