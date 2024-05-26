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

package com.gluonhq.jfxapps.metadata.bean;

import com.gluonhq.jfxapps.metadata.util.Resources;

/**
 * The base class for all meta-data used for describing JavaBeans, properties,
 * and events.
 *
 * @author Richard
 */
public abstract class AbstractMetaData {
    /**
     * A special category name which is used to indicate that this particular
     * JavaBean, Property, or Event is only intended for expert usage. An IDE
     * might group these specially, defaulting them to hidden for example.
     */
    public static final String EXPERT = "Expert";

    /**
     * A special category name which is used to indicate that this particular
     * JavaBean, Property, or Event should be hidden from the user and not
     * exposed via the visual tool.
     */
    public static final String HIDDEN = "Hidden";

    /**
     * A special category name which is used to indicate that this particular
     * JavaBean, Property, or Event is preferred, and should in some way
     * be more prominent.
     */
    public static final String PREFERRED = "Preferred";

    /**
     * The name associated with this JavaBean, Property, or Event. This
     * name is immutable, and <strong>must</strong> be exactly the same
     * as the source file name. This value can only be set by the introspector,
     * and cannot be customized via annotation.
     */
    private String name;
//
//    /**
//     * The displayName of the JavaBean, Property, or Event. If prefixed with %,
//     * the name will be looked up via a resource bundle.
//     * TODO how to spec this? You can have Java based resource bundles, or you
//     * can have .properties based resource bundles, or even XML based resource
//     * bundles. So the displayName itself might not be useful except with a
//     * DesignInfo. Really all this meta-data is design-time related, maybe it
//     * should be moved to the design.author package?
//     */
//    private String displayName;
//
//    /**
//     * A short description of the JavaBean, Property, or Event. If prefixed with
//     * %, the name will be looked up via a resource bundle.
//     */
//    private String shortDescription;
//
//    /**
//     * The category for this meta-data. This can be any value, including null.
//     */
//    private String category;
//
//    /**
//     * The bundles associated with this class
//     */
//    protected final Resources bundle;

    /**
     * Do not permit anybody outside this package from extending MetaData
     */
    protected AbstractMetaData(String name) {
    	 //this.bundle = resource;
    	 this.name = name;
    }

    public final String getName() { return name; }

    protected void setName(String name) {
        this.name = name;
    }

	@Override
	public String toString() {
		return "MetaData [name=" + name + "]";
	}


}
