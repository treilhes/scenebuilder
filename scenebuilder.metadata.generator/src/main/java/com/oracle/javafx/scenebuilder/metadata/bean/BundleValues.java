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
package com.oracle.javafx.scenebuilder.metadata.bean;

public interface BundleValues {

	//class level
	final String FREE_POSITIONING = "freeChildPositioning";
	final String RESIZE_WHEN_TOP_ELEMENT = "resizeWhenTop";
	final String VERSION = "version";
	final String QUALIFIERS = "qualifiers";
	final String CATEGORY = "category";
	final String DESCRIPTION_PROPERTY = "descriptionProperty";
	final String LABEL_MUTATION_LAMBDA = "labelMutation";

	//property level
	final String INSPECTOR_SECTION = "section";
	final String INSPECTOR_SUBSECTION = "subSection";
	final String ORDER = "order";
	final String METACLASS = "class";
	final String TMP_METACLASS_KIND = "classKind";
	final String IS_COMPONENT = "component";
	final String IS_COLLECTION = "collection";
	final String COLLECTION_TYPE = "collectionType";
	final String CONTENT_TYPE = "type";
	final String CHILD_LABEL_MUTATION_LAMBDA = "childLabelMutation";
	final String NULL_EQUIVALENT = "nullEquivalent";

	final String MAIN = "main";
	final String HIDDEN = "hidden";
	final String VISIBILITY = "visibility";

	//both
	final String IMAGE = "image";
	final String IMAGE_X2 = "imagex2";


	//qualifier level
    final String FXML = "fxml";
    final String LABEL = "label";
    final String LAMBDA_CHECK = "lambdaCheck";


	final String HIDDEN_PROPERTIES = "hiddens";
    final String DISPLAY_NAME = "displayName";






}
