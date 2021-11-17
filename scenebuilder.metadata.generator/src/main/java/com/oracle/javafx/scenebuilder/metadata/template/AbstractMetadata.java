package com.oracle.javafx.scenebuilder.metadata.template;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.oracle.javafx.scenebuilder.metadata.model.Component;
import com.oracle.javafx.scenebuilder.metadata.model.Property;
import com.oracle.javafx.scenebuilder.metadata.util.ReflectionUtils;

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

				if (valueCls == String.class) {
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
