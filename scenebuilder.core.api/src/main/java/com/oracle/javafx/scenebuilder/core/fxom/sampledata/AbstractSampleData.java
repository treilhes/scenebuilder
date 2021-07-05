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

package com.oracle.javafx.scenebuilder.core.fxom.sampledata;

import javafx.scene.paint.Color;

/**
 *
 */
abstract class AbstractSampleData {
    
    private static final String[] lorem = {
        "Lorem ipsum ", //NOCHECK
        "dolor sit amet, ", //NOCHECK
        "consectetur adipiscing elit. ", //NOCHECK
        "Donec eu justo ", //NOCHECK
        "at tortor porta ", //NOCHECK
        "commodo nec vitae magna. ", //NOCHECK
        "Maecenas tempus ", //NOCHECK
        "hendrerit elementum. ", //NOCHECK
        "Nam sed mi ", //NOCHECK
        "a lorem tincidunt ", //NOCHECK
        "luctus sed non sem. ", //NOCHECK
        "Aliquam erat volutpat. ", //NOCHECK
        "Donec tempus egestas ", //NOCHECK
        "libero a cursus. ", //NOCHECK
        "In lectus nunc, ", //NOCHECK
        "dapibus vel suscipit vel, ", //NOCHECK
        "faucibus eget justo. ", //NOCHECK
        "Aliquam erat volutpat. ", //NOCHECK
        "Nulla facilisi. ", //NOCHECK
        "Donec at enim ipsum, ", //NOCHECK
        "sed facilisis leo. ", //NOCHECK
        "Aliquam tincidunt ", //NOCHECK
        "adipiscing euismod. ", //NOCHECK
        "Sed aliquet eros ", //NOCHECK
        "ut libero congue ", //NOCHECK
        "quis bibendum ", //NOCHECK
        "felis ullamcorper. ", //NOCHECK
        "Vestibulum ipsum ante, ", //NOCHECK
        "semper eu sollicitudin rutrum, ", //NOCHECK
        "consectetur a enim. ", //NOCHECK
        "Ut eget nisl sed turpis ", //NOCHECK
        "egestas viverra ", //NOCHECK
        "ut tristique sem. ", //NOCHECK
        "Nunc in neque nulla. " //NOCHECK
    };
    
    private final static Color[] colors = {
        Color.AZURE, Color.CHARTREUSE, Color.CRIMSON, Color.DARKCYAN
    };
    
    private static final String[] alphabet = {
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", //NOCHECK
        "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" //NOCHECK
    };
    
    public abstract void applyTo(Object sceneGraphObject);
    public abstract void removeFrom(Object sceneGraphObject);
    
    
    /*
     * Utilites for subclasses
     */
    
    protected static String lorem(int index) {
        return lorem[index % lorem.length];
    }
    
    protected static Color color(int index) {
        return colors[index % colors.length];
    }

    protected static String alphabet(int index) {
        return alphabet[index % alphabet.length];
    }
}
