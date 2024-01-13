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
package com.oracle.javafx.scenebuilder.api.ui.dock;

import java.net.URL;
import java.util.UUID;

import com.oracle.javafx.scenebuilder.api.ui.dock.annotation.ViewAttachment;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.MenuButton;

public interface View {

    public static URL VIEW_ICON_MISSING = View.class.getResource("ViewIconMissing.png");
    public static URL VIEW_ICON_MISSING_X2 = View.class.getResource("ViewIconMissing@2x.png");

    StringProperty nameProperty();
	//ViewManager getViewManager();
	ViewSearch getSearchController();
	ViewContent getViewController();
	void populateMenu(MenuButton menuButton);
	void clearMenu(MenuButton menuButton);

//	void shown();
//	void hidden();
//	boolean isVisible();

	BooleanProperty visibleProperty();
    default void setVisible(boolean visible) {
        visibleProperty().set(visible);
    }
    default boolean isVisible() {
        return visibleProperty().get();
    }

	ObjectProperty<Dock> parentDockProperty();
	default void setParentDock(Dock dock) {
	    parentDockProperty().set(dock);
	}
	default Dock getParentDock() {
        return parentDockProperty().get();
    }
	/**
    *
    */
    void notifyFocused();

	public static ViewAttachment viewDescriptorAnnotation(Class<? extends View> cls) {
        ViewAttachment viewDescriptor = cls.getAnnotation(ViewAttachment.class);
        if (viewDescriptor == null) {
            throw new RuntimeException("Class implementing View interface must be annotated with @ViewDescriptor");
        }
        return viewDescriptor;
    }

	public default String getViewName() {
	    return viewDescriptorAnnotation(this.getClass()).name();
    }

	public default UUID getId() {
	    return UUID.fromString(viewDescriptorAnnotation(this.getClass()).id());
    }

	public static String getViewName(Class<? extends View> cls) {
        return viewDescriptorAnnotation(cls).name();
    }

    public static UUID getId(Class<? extends View> cls) {
        return UUID.fromString(viewDescriptorAnnotation(cls).id());
    }

    public static UUID getPrefDockId(Class<? extends View> cls) {
        String pref = viewDescriptorAnnotation(cls).prefDockId();
        return (pref != null && !pref.isEmpty()) ? UUID.fromString(pref) : null;
    }
    public static boolean isOpenOnStart(Class<? extends View> cls) {
        return viewDescriptorAnnotation(cls).openOnStart();
    }
    public static boolean isSelectOnStart(Class<? extends View> cls) {
        return viewDescriptorAnnotation(cls).selectOnStart();
    }

    public static int getOrder(Class<? extends View> cls) {
        return viewDescriptorAnnotation(cls).order();
    }


}