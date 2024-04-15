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
package com.gluonhq.jfxapps.metadata.template;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.gluonhq.jfxapps.metadata.model.Component;
import com.gluonhq.jfxapps.metadata.model.Property;
import com.gluonhq.jfxapps.metadata.util.ReflectionUtils;

import javafx.geometry.BoundingBox;
import javafx.geometry.Point3D;
import javafx.scene.text.Font;

public class AbstractMetadata extends HashMap<String, Object> {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public AbstractMetadata() {
		super();
	}

	public AbstractMetadata(Map<? extends String, ? extends Object> m) {
		super(m);
	}

	protected String computeStringValue(Component component, Property property) {

		Object def = property.getRaw().getDefaultValue();
		String defaultValue = null;
		String nullEquivalent = null;

		if (def != null) {
		    Class propertyReturnType = property.getRaw().getType();
			Class valueCls = def.getClass();
			//if ((pm instanceof EnumerationPropertyMetadata) && v.getValueClass().isEnum()) {
			if (propertyReturnType.isEnum()) {

				String enumPack = property.getRaw().getType().getName().replace("$", ".");
				try {
					Enum.valueOf(propertyReturnType, def.toString()); // check enum value exists
					defaultValue = enumPack + "." + def.toString();
				} catch (Exception e) {
					nullEquivalent = def.toString();
					defaultValue = null;
				}

			} else {

				if (propertyReturnType == String.class) {
					defaultValue = def.toString();
					if (defaultValue.toString().startsWith("<html>")) {
						defaultValue = defaultValue.toString().replace("\"", "\\\"");
					}
					defaultValue = "\"" + defaultValue + "\"";
				} else if (property.getRaw().isCollection() && (def instanceof Collection) && ((Collection)def).isEmpty()) {
					defaultValue = "java.util.Collections.emptyList()";
				} else if (property.getRaw().isCollection() && def instanceof Collection) {
					Collection c = (Collection)def;
					String items = (String) c.stream().map(i -> "\"" + i.toString() + "\"").collect(Collectors.joining(","));
					defaultValue = String.format("java.util.Arrays.asList(%s)", items);
				} else if (property.getRaw().isCollection() && valueCls.isArray()) {
					Object[] arrObject = ReflectionUtils.convertToObjectArray(def);
					if (arrObject.length == 0) {
						defaultValue = "java.util.Collections.emptyList()";
					} else {
						String items = Arrays.stream(arrObject)
								.map(i -> ReflectionUtils.getPrimitiveDeclaration(property.getRaw().getCollectionType(), i))
								.collect(Collectors.joining(","));
						defaultValue = String.format("java.util.Arrays.asList(%s)", items);
					}


				} else if (def instanceof Boolean) {
					defaultValue = def.toString();
				}

				if (defaultValue == null) {
					defaultValue = ReflectionUtils.findStaticMemberByValue(def.getClass(), def);
				}
				if (defaultValue == null) {
					defaultValue = ReflectionUtils.findStaticGetMethodByValue(def.getClass(), def);
				}
				if (defaultValue == null) {
				    Class<?> componentClass = component.getRaw().getType();
                    defaultValue = ReflectionUtils.findStaticMemberByValue(componentClass, def);
				}

				if (defaultValue == null && def instanceof BoundingBox) {
					BoundingBox dv = (BoundingBox)def;
					defaultValue = String.format("new javafx.geometry.BoundingBox(%s, %s, %s, %s, %s, %s)",
							dv.getMinX(),dv.getMinY(),dv.getMinZ(),
							dv.getWidth(), dv.getHeight(), dv.getDepth()
							);
				}

				if (defaultValue == null && def instanceof Font) {
					Font dv = (Font)def;
					defaultValue = String.format("javafx.scene.text.Font.font(\"%s\", %s)",
							dv.getFamily(),dv.getSize());
				}

				if (defaultValue == null && def instanceof Point3D) {
					Point3D dv = (Point3D)def;
					defaultValue = String.format("new javafx.geometry.Point3D(%s, %s, %s)",
							dv.getX(),dv.getY(), dv.getZ());
				}


				if (defaultValue == null) {
					defaultValue = def.toString();
				}
			}
		}

		if (defaultValue == null && nullEquivalent == null) {
			defaultValue = "null";
		}

		return defaultValue;
	}
}
