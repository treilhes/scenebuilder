package com.gluonhq.jfxapps.metadata.template.scenebuilderx;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.gluonhq.jfxapps.metadata.bean.PropertyMetaData;
import com.gluonhq.jfxapps.metadata.model.Component;
import com.gluonhq.jfxapps.metadata.model.Property;
import com.gluonhq.jfxapps.metadata.model.Property.Type;
import com.gluonhq.jfxapps.metadata.template.AbstractMetadata;

public class SbxMetadata extends AbstractMetadata {

    public static final String PROPERTIES_KEY = "properties";

    public static final String COMPONENT_META_PARAMS_KEY = "metaParameters";

    public static final String MISSING_INCLUDE_ELEMENT_FLAG = "missingIncludeElement";

    private TreeSet<Property> properties = new TreeSet<>();

    Comparator<Property> cmpProp = Comparator.comparing((Property c) -> (String) c.getCustom().get("memberName"));

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public SbxMetadata(Map<Component, Set<Property>> providedComponents) {

        properties = new TreeSet<>(cmpProp);

        put(PROPERTIES_KEY, properties);
        put(MISSING_INCLUDE_ELEMENT_FLAG, "xx");

        TreeMap<Component, HashSet<Class<?>>> componentsMetadataParameters = new TreeMap<>();

        for (Map.Entry<Component, Set<Property>> e : providedComponents.entrySet()) {
            Component component = e.getKey();

            Set<Property> properties = e.getValue();
            TreeSet<Property> props = new TreeSet<>(properties);
            HashSet<Class<?>> metadataTypes = new HashSet<>();

            properties.clear();

            for (Property p : props) {
                Property property = handlePropertyTransform(component, p);
                properties.add(property);
                addProperty(property);

                if (property.getType() == Type.COMPONENT && property.getRaw().getContentType() != null) {
                    metadataTypes.add(property.getRaw().getContentType());
                }
            }
            componentsMetadataParameters.put(component, metadataTypes);
        }

        put(COMPONENT_META_PARAMS_KEY, componentsMetadataParameters);
    }

    private Property handlePropertyTransform(Component component, Property property) {
        PropertyMetaData p = property.getRaw();

        String newMemberName = p.getName();
        String newMetadataMemberName = p.getName();

        newMemberName = p.isStatic() ? p.getResidenceClass().getSimpleName() + "_" + newMemberName : newMemberName;

        if (property.getType() == Type.COMPONENT) {

            property.getCustom().put("memberName", newMemberName);
            property.getCustom().put("metadataMemberName", newMetadataMemberName);

        } else if (property.getType() == Type.VALUE) {
            Object def = p.getDefaultValue();
            String defaultValue = computeStringValue(component, property);
            String nullEquivalent = null;

            if (p.getType().isEnum() && def != null && defaultValue == null) {
                nullEquivalent = def.toString();
            }

            property.getCustom().put("memberName", newMemberName);
            property.getCustom().put("metadataMemberName", newMetadataMemberName);
            property.getCustom().put("defaultValue", defaultValue);
            property.getCustom().put("nullEquivalent", nullEquivalent);

        }
        return property;
    }

    public void addProperty(Property property) {
        properties.add(property);
    }

}
