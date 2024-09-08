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
package com.oracle.javafx.scenebuilder.template.templates;

import java.util.ArrayList;

import org.graalvm.compiler.lir.CompositeValue.Component;

import com.gluonhq.jfxapps.core.api.Size;
import com.gluonhq.jfxapps.core.api.template.AbstractTemplate;
import com.gluonhq.jfxapps.core.api.template.AbstractTemplateGroup;
import com.oracle.javafx.scenebuilder.api.theme.Theme;

public class DefaultTemplateList {
    
    @Component
    public static class DefaultGroup extends AbstractTemplateGroup{
        public DefaultGroup() {
            super("0", null);
        }
    }

    @Component
    public static class DesktopGroup extends AbstractTemplateGroup{
        public DesktopGroup() {
            super("A", "template.title.header.desktop");
        }
    }
    
    @Component
    public static class PhoneGroup extends AbstractTemplateGroup{
        public PhoneGroup() {
            super("B", "template.title.header.phone");
        }
    }
    
    @Component
    public static class TabletGroup extends AbstractTemplateGroup{
        public TabletGroup() {
            super("C", "template.title.header.tablet");
        }
    }
    
    @Component
    public static class OtherGroup extends AbstractTemplateGroup{
        public OtherGroup() {
            super("D", "template.title.header.other");
        }
    }
    
    @Component
    public static class EmptyTemplate extends AbstractTemplate{

        public EmptyTemplate(@Autowired DefaultGroup group) {
            super(group, "A", "template.title.new.empty.app", 
                    null, 
                    Size.SIZE_640x480,
                    null,
                    null,
                    new ArrayList<Class<? extends Theme>>());
        }
        
    }
    
    @Component
    public static class BasicDesktopTemplate extends AbstractTemplate{

        public BasicDesktopTemplate(@Autowired DesktopGroup group) {
            super(group, "A", "template.title.new.basic.desktop.app", 
                    BasicDesktopTemplate.class.getResource("BasicDesktopApplication.fxml"),
                    Size.SIZE_640x480,
                    BasicDesktopTemplate.class.getResource("basic_desktop.png"),
                    BasicDesktopTemplate.class.getResource("basic_desktop@2x.png"),
                    new ArrayList<Class<? extends Theme>>());
        }
        
    }
    
    @Component
    public static class ComplexDesktopTemplate extends AbstractTemplate{

        public ComplexDesktopTemplate(@Autowired DesktopGroup group) {
            super(group, "B", "template.title.new.complex.desktop.app", 
                    ComplexDesktopTemplate.class.getResource("ComplexDesktopApplication.fxml"),
                    Size.SIZE_640x480,
                    ComplexDesktopTemplate.class.getResource("complex_desktop.png"),
                    ComplexDesktopTemplate.class.getResource("complex_desktop@2x.png"),
                    new ArrayList<Class<? extends Theme>>());
        }
        
    }
}
