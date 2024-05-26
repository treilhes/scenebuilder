/*
 * Copyright (c) 2016, 2017 Gluon and/or its affiliates.
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
 *  - Neither the name of Oracle Corporation nor the names of its
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
package ${package};

/*
 * THIS CODE IS AUTOMATICALLY GENERATED !
 */

import com.gluonhq.charm.glisten.control.BottomNavigation;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMIntrinsic;
import com.oracle.javafx.scenebuilder.kit.metadata.klass.ComponentClassMetadata;
import com.oracle.javafx.scenebuilder.kit.metadata.property.ComponentPropertyMetadata;
import com.oracle.javafx.scenebuilder.kit.metadata.property.PropertyMetadata;
import com.oracle.javafx.scenebuilder.kit.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.kit.metadata.property.value.*;
import com.oracle.javafx.scenebuilder.kit.metadata.property.value.effect.EffectPropertyMetadata;
import com.oracle.javafx.scenebuilder.kit.metadata.property.value.keycombination.KeyCombinationPropertyMetadata;
import com.oracle.javafx.scenebuilder.kit.metadata.property.value.list.ButtonTypeListPropertyMetadata;
import com.oracle.javafx.scenebuilder.kit.metadata.property.value.list.DoubleListPropertyMetadata;
import com.oracle.javafx.scenebuilder.kit.metadata.property.value.list.StringListPropertyMetadata;
import com.oracle.javafx.scenebuilder.kit.metadata.property.value.list.TickMarkListPropertyMetadata;
import com.oracle.javafx.scenebuilder.kit.metadata.property.value.paint.ColorPropertyMetadata;
import com.oracle.javafx.scenebuilder.kit.metadata.property.value.paint.PaintPropertyMetadata;
import com.oracle.javafx.scenebuilder.kit.metadata.util.InspectorPath;
import com.oracle.javafx.scenebuilder.kit.metadata.util.InspectorPathComparator;
import com.oracle.javafx.scenebuilder.kit.metadata.util.PropertyName;

import javafx.scene.Scene;
import javafx.scene.control.SelectionMode;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 *
 */
public class Metadata {

    private static Metadata metadata = null;


    private final Map<Class<?>, ComponentClassMetadata> componentClassMap = new HashMap<>();
    private final Map<Class<?>, ComponentClassMetadata> customComponentClassMap = new WeakHashMap<>();
    private final Set<PropertyName> hiddenProperties = new HashSet<>();
    private final Set<PropertyName> parentRelatedProperties = new HashSet<>();
    private final List<String> sectionNames = new ArrayList<>();
    private final Map<String, List<String>> subSectionMap = new HashMap<>();

    public final InspectorPathComparator INSPECTOR_PATH_COMPARATOR
            = new InspectorPathComparator(sectionNames, subSectionMap);

    public static synchronized Metadata getMetadata() {
        if (metadata == null) {
            metadata = new Metadata();
        }
        return metadata;
    }

    public ComponentClassMetadata queryComponentMetadata(Class<?> componentClass) {
        final ComponentClassMetadata result;


        final ComponentClassMetadata componentMetadata
                = componentClassMap.get(componentClass);
        if (componentMetadata != null) {
            // componentClass is a certified component
            result = componentMetadata;
        } else {
            // componentClass is a custom component
            final ComponentClassMetadata customMetadata
                    = customComponentClassMap.get(componentClass);
            if (customMetadata != null) {
                // componentClass has already been introspected
                result = customMetadata;
            } else {
                // componentClass must be introspected
                // Let's find the first certified ancestor
                Class<?> ancestorClass = componentClass.getSuperclass();
                ComponentClassMetadata ancestorMetadata = null;
                while ((ancestorClass != null) && (ancestorMetadata == null)) {
                    ancestorMetadata = componentClassMap.get(ancestorClass);
                    ancestorClass = ancestorClass.getSuperclass();
                }
                final MetadataIntrospector introspector
                        = new MetadataIntrospector(componentClass, ancestorMetadata);
                result = introspector.introspect();
                customComponentClassMap.put(componentClass, result);
            }
        }

        return result;
    }

    public Set<PropertyMetadata> queryProperties(Class<?> componentClass) {
        final Map<PropertyName, PropertyMetadata> result = new HashMap<>();
        ComponentClassMetadata classMetadata = queryComponentMetadata(componentClass);

        while (classMetadata != null) {
            for (PropertyMetadata pm : classMetadata.getProperties()) {
                if (result.containsKey(pm.getName()) == false) {
                    result.put(pm.getName(), pm);
                }
            }
            classMetadata = classMetadata.getParentMetadata();
        }

        return new HashSet<>(result.values());
    }

    public Set<PropertyMetadata> queryProperties(Collection<Class<?>> componentClasses) {
        final Set<PropertyMetadata> result = new HashSet<>();

        int count = 0;
        for (Class<?> componentClass : componentClasses) {
            final Set<PropertyMetadata> propertyMetadata = queryProperties(componentClass);
            if (count == 0) {
                result.addAll(propertyMetadata);
            } else {
                result.retainAll(propertyMetadata);
            }
            count++;
        }

        return result;
    }

    public Set<ComponentPropertyMetadata> queryComponentProperties(Class<?> componentClass) {
        final Set<ComponentPropertyMetadata> result = new HashSet<>();

        for (PropertyMetadata propertyMetadata : queryProperties(Arrays.asList(componentClass))) {
            if (propertyMetadata instanceof ComponentPropertyMetadata) {
                result.add((ComponentPropertyMetadata) propertyMetadata);
            }
        }
        return result;
    }

    public Set<ValuePropertyMetadata> queryValueProperties(Set<Class<?>> componentClasses) {
        final Set<ValuePropertyMetadata> result = new HashSet<>();
        for (PropertyMetadata propertyMetadata : queryProperties(componentClasses)) {
            if (propertyMetadata instanceof ValuePropertyMetadata) {
                result.add((ValuePropertyMetadata) propertyMetadata);
            }
        }
        return result;
    }

    public PropertyMetadata queryProperty(Class<?> componentClass, PropertyName targetName) {
        final Set<PropertyMetadata> propertyMetadataSet = queryProperties(componentClass);
        final Iterator<PropertyMetadata> iterator = propertyMetadataSet.iterator();
        PropertyMetadata result = null;

        while ((result == null) && iterator.hasNext()) {
            final PropertyMetadata propertyMetadata = iterator.next();
            if (propertyMetadata.getName().equals(targetName)) {
                result = propertyMetadata;
            }
        }

        return result;
    }

    public ValuePropertyMetadata queryValueProperty(FXOMInstance fxomInstance, PropertyName targetName) {
        final ValuePropertyMetadata result;
        assert fxomInstance != null;
        assert targetName != null;

        if (fxomInstance.getSceneGraphObject() == null) {
            // FXOM object is unresolved
            result = null;
        } else {
            final Class<?> componentClass;
            if (fxomInstance.getDeclaredClass() == FXOMIntrinsic.class) {
                componentClass = fxomInstance.getDeclaredClass();
            } else {
                componentClass = fxomInstance.getSceneGraphObject().getClass();
            }

            final PropertyMetadata m = Metadata.getMetadata().queryProperty(componentClass, targetName);
            if (m instanceof ValuePropertyMetadata) {
                result = (ValuePropertyMetadata) m;
            } else {
                result = null;
            }
        }

        return result;
    }


    public Collection<ComponentClassMetadata> getComponentClasses() {
        return componentClassMap.values();
    }

    public Set<PropertyName> getHiddenProperties() {
        return hiddenProperties;
    }

    public boolean isPropertyTrimmingNeeded(PropertyName name) {
        final boolean result;

        if (name.getResidenceClass() != null) {
            // It's a static property eg GridPane.rowIndex
            // All static property are "parent related" and needs trimming
            result = true;
        } else {
            result = parentRelatedProperties.contains(name);
        }

        return result;
    }


    // Abstract Component Classes

	<#list components as component>
	<#if component.custom["abstract"] == true>
    private final ComponentClassMetadata ${component.raw.type.simpleName}Metadata =
            new ComponentClassMetadata(${component.raw.type.name?replace("$", ".")}.class, <#if (component.parent)??>${(component.parent.raw.type.simpleName)}Metadata<#else>null</#if>);
    </#if>
    </#list>


    // Other Component Classes (in alphabetical order)

    <#list components as component>
    <#if component.custom["abstract"] == false>
    private final ComponentClassMetadata ${component.raw.type.simpleName}Metadata =
            new ComponentClassMetadata(${component.raw.type.name?replace("$", ".")}.class, <#if (component.parent)??>${(component.parent.raw.type.simpleName)}Metadata<#else>null</#if>);
    </#if>
    </#list>
    private final ComponentClassMetadata IncludeElementMetadata =
            new ComponentClassMetadata(com.oracle.javafx.scenebuilder.kit.fxom.FXOMIntrinsic.class, null);

    // Property Names

    <#list properties as property>
    private final PropertyName ${property.custom["memberName"]}Name =
            new PropertyName("${property.raw.name}"<#if property.raw.static == true>, ${property.raw.residenceClass.name}.class</#if>);
    </#list>

    // Property Metadata

    <#list propertyMetas as property>
<#if property.type == "VALUE">
    private final ValuePropertyMetadata ${property.custom["metadataMemberName"]}PropertyMetadata =
            new ${property.raw.metadataClass.simpleName}(
                ${property.custom["memberName"]}Name,
	<#if property.raw.type.enum == true>
                ${property.raw.type.name?replace("$", ".")}.class,
	</#if>
	<#if property.raw.kind??>
                ${property.raw.kind},
	</#if>
	<#if property.custom["nullEquivalent"]??>
                "property.custom["nullEquivalent"]", /* null equivalent */
	</#if>
                ${property.raw.readWrite}, /* readWrite */
	<#if property.custom["defaultValue"]??>
                ${property.custom["defaultValue"]}, <#if property.raw.readWrite == true>/* defaultValue */</#if><#if property.raw.readWrite == false>/* No defaultValue for R/O property */</#if>
	</#if>
                new InspectorPath("${property.raw.section}", "${property.raw.subSection}", ${property.raw.order}));
</#if>
<#if property.type == "COMPONENT">
    private final ComponentPropertyMetadata ${property.custom["metadataMemberName"]}PropertyMetadata =
            new ComponentPropertyMetadata(
                ${property.custom["memberName"]}Name,
                <#if property.raw.collection == true>${property.raw.collectionType.simpleName}<#else>${property.raw.type.simpleName}</#if>Metadata,
                ${property.raw.collection}); /* collection */
</#if>
    </#list>


<#if missingIncludeElement??>
        private final PropertyName sourceName =
            new PropertyName("source");

        private final ValuePropertyMetadata includeFxmlPropertyMetadata =
            new StringListPropertyMetadata(
                sourceName,
                true, /* readWrite */
                Collections.emptyList(), /* defaultValue */
                new InspectorPath("Properties", "Include FXML file", 2));
</#if>

    private Metadata() {

        // Populate componentClassMap
        <#list allComponents as component>
        componentClassMap.put(${component.raw.type.simpleName}Metadata.getKlass(), ${component.raw.type.simpleName}Metadata);
        </#list>
        componentClassMap.put(IncludeElementMetadata.getKlass(), IncludeElementMetadata);

        // ComponentMetadata -> PropertyMetadata
        <#list componentProps as component, propertyMetas>
        <#list propertyMetas as property>
        ${component.raw.type.simpleName}Metadata.getProperties().add(${property.custom["metadataMemberName"]}PropertyMetadata);
        </#list>

        </#list>

        IncludeElementMetadata.getProperties().add(AnchorPane_bottomAnchorPropertyMetadata);
        IncludeElementMetadata.getProperties().add(AnchorPane_leftAnchorPropertyMetadata);
        IncludeElementMetadata.getProperties().add(AnchorPane_rightAnchorPropertyMetadata);
        IncludeElementMetadata.getProperties().add(AnchorPane_topAnchorPropertyMetadata);
        IncludeElementMetadata.getProperties().add(BorderPane_alignmentPropertyMetadata);
        IncludeElementMetadata.getProperties().add(FlowPane_marginPropertyMetadata);
        IncludeElementMetadata.getProperties().add(GridPane_columnIndexPropertyMetadata);
        IncludeElementMetadata.getProperties().add(GridPane_columnSpanPropertyMetadata);
        IncludeElementMetadata.getProperties().add(GridPane_halignmentPropertyMetadata);
        IncludeElementMetadata.getProperties().add(GridPane_hgrowPropertyMetadata);
        IncludeElementMetadata.getProperties().add(GridPane_rowIndexPropertyMetadata);
        IncludeElementMetadata.getProperties().add(GridPane_rowSpanPropertyMetadata);
        IncludeElementMetadata.getProperties().add(GridPane_valignmentPropertyMetadata);
        IncludeElementMetadata.getProperties().add(GridPane_vgrowPropertyMetadata);
        IncludeElementMetadata.getProperties().add(HBox_hgrowPropertyMetadata);
        IncludeElementMetadata.getProperties().add(StackPane_alignmentPropertyMetadata);
        IncludeElementMetadata.getProperties().add(TilePane_alignmentPropertyMetadata);
        IncludeElementMetadata.getProperties().add(VBox_vgrowPropertyMetadata);
        IncludeElementMetadata.getProperties().add(layoutXPropertyMetadata);
        IncludeElementMetadata.getProperties().add(layoutYPropertyMetadata);
        IncludeElementMetadata.getProperties().add(maxHeight_COMPUTED_PropertyMetadata);
        IncludeElementMetadata.getProperties().add(maxWidth_COMPUTED_PropertyMetadata);
        IncludeElementMetadata.getProperties().add(minHeight_COMPUTED_PropertyMetadata);
        IncludeElementMetadata.getProperties().add(minWidth_COMPUTED_PropertyMetadata);
        IncludeElementMetadata.getProperties().add(prefHeight_COMPUTED_PropertyMetadata);
        IncludeElementMetadata.getProperties().add(prefWidth_COMPUTED_PropertyMetadata);
        IncludeElementMetadata.getProperties().add(rotatePropertyMetadata);
        IncludeElementMetadata.getProperties().add(rotationAxisPropertyMetadata);
        IncludeElementMetadata.getProperties().add(scaleXPropertyMetadata);
        IncludeElementMetadata.getProperties().add(scaleYPropertyMetadata);
        IncludeElementMetadata.getProperties().add(scaleZPropertyMetadata);
        IncludeElementMetadata.getProperties().add(translateXPropertyMetadata);
        IncludeElementMetadata.getProperties().add(translateYPropertyMetadata);
        IncludeElementMetadata.getProperties().add(translateZPropertyMetadata);
        IncludeElementMetadata.getProperties().add(layoutBoundsPropertyMetadata);
        IncludeElementMetadata.getProperties().add(boundsInLocalPropertyMetadata);
        IncludeElementMetadata.getProperties().add(boundsInParentPropertyMetadata);
        IncludeElementMetadata.getProperties().add(baselineOffsetPropertyMetadata);
        IncludeElementMetadata.getProperties().add(resizable_Boolean_ro_PropertyMetadata);
        IncludeElementMetadata.getProperties().add(contentBiasPropertyMetadata);
        IncludeElementMetadata.getProperties().add(snapToPixelPropertyMetadata);
        IncludeElementMetadata.getProperties().add(effectiveNodeOrientationPropertyMetadata);
        IncludeElementMetadata.getProperties().add(includeFxmlPropertyMetadata);

        // Populates hiddenProperties
        hiddenProperties.add(new PropertyName("activated"));
        hiddenProperties.add(new PropertyName("alignWithContentOrigin"));
        hiddenProperties.add(new PropertyName("armed"));
        hiddenProperties.add(new PropertyName("anchor"));
        hiddenProperties.add(new PropertyName("antiAliasing"));
        hiddenProperties.add(new PropertyName("border"));
        hiddenProperties.add(new PropertyName("background"));
        hiddenProperties.add(new PropertyName("caretPosition"));
        hiddenProperties.add(new PropertyName("camera"));
        hiddenProperties.add(new PropertyName("cellFactory"));
        hiddenProperties.add(new PropertyName("cellValueFactory"));
        hiddenProperties.add(new PropertyName("characters"));
        hiddenProperties.add(new PropertyName("childrenUnmodifiable"));
        hiddenProperties.add(new PropertyName("chronology"));
        hiddenProperties.add(new PropertyName("class"));
        hiddenProperties.add(new PropertyName("comparator"));
        hiddenProperties.add(new PropertyName("converter"));
        hiddenProperties.add(new PropertyName("controlCssMetaData"));
        hiddenProperties.add(new PropertyName("cssMetaData"));
        hiddenProperties.add(new PropertyName("customColors"));
        hiddenProperties.add(new PropertyName("data"));
        hiddenProperties.add(new PropertyName("dayCellFactory"));
        hiddenProperties.add(new PropertyName("depthBuffer"));
        hiddenProperties.add(new PropertyName("disabled"));
        hiddenProperties.add(new PropertyName("dividers"));
        hiddenProperties.add(new PropertyName("editingCell"));
        hiddenProperties.add(new PropertyName("editingIndex"));
        hiddenProperties.add(new PropertyName("editingItem"));
        hiddenProperties.add(new PropertyName("editor"));
        hiddenProperties.add(new PropertyName("engine"));
        hiddenProperties.add(new PropertyName("eventDispatcher"));
        hiddenProperties.add(new PropertyName("expandedPane"));
        hiddenProperties.add(new PropertyName("filter"));
        hiddenProperties.add(new PropertyName("focused"));
        hiddenProperties.add(new PropertyName("focusModel"));
        hiddenProperties.add(new PropertyName("graphicsContext2D"));
        hiddenProperties.add(new PropertyName("hover"));
        hiddenProperties.add(new PropertyName("inputMethodRequests"));
        hiddenProperties.add(new PropertyName("localToParentTransform"));
        hiddenProperties.add(new PropertyName("localToSceneTransform"));
        hiddenProperties.add(new PropertyName("managed"));
        hiddenProperties.add(new PropertyName("mediaPlayer"));
        hiddenProperties.add(new PropertyName("needsLayout"));
        hiddenProperties.add(new PropertyName("nodeColumnEnd", javafx.scene.layout.GridPane.class));
        hiddenProperties.add(new PropertyName("nodeColumnIndex", javafx.scene.layout.GridPane.class));
        hiddenProperties.add(new PropertyName("nodeColumnSpan", javafx.scene.layout.GridPane.class));
        hiddenProperties.add(new PropertyName("nodeHgrow", javafx.scene.layout.GridPane.class));
        hiddenProperties.add(new PropertyName("nodeMargin", javafx.scene.layout.BorderPane.class));
        hiddenProperties.add(new PropertyName("nodeRowEnd", javafx.scene.layout.GridPane.class));
        hiddenProperties.add(new PropertyName("nodeRowIndex", javafx.scene.layout.GridPane.class));
        hiddenProperties.add(new PropertyName("nodeRowSpan", javafx.scene.layout.GridPane.class));
        hiddenProperties.add(new PropertyName("nodeVgrow", javafx.scene.layout.GridPane.class));
        hiddenProperties.add(new PropertyName("ownerWindow"));
        hiddenProperties.add(new PropertyName("ownerNode"));
        hiddenProperties.add(new PropertyName("pageFactory"));
        hiddenProperties.add(new PropertyName("paragraphs"));
        hiddenProperties.add(new PropertyName("parent"));
        hiddenProperties.add(new PropertyName("parentColumn"));
        hiddenProperties.add(new PropertyName("parentMenu"));
        hiddenProperties.add(new PropertyName("parentPopup"));
        hiddenProperties.add(new PropertyName("pressed"));
        hiddenProperties.add(new PropertyName("properties"));
        hiddenProperties.add(new PropertyName("pseudoClassStates"));
        hiddenProperties.add(new PropertyName("redoable"));
        hiddenProperties.add(new PropertyName("root"));
        hiddenProperties.add(new PropertyName("rowFactory"));
        hiddenProperties.add(new PropertyName("scene"));
        hiddenProperties.add(new PropertyName("selection"));
        hiddenProperties.add(new PropertyName("selectionModel"));
        hiddenProperties.add(new PropertyName("selectedText"));
        hiddenProperties.add(new PropertyName("showing"));
        hiddenProperties.add(new PropertyName("sortPolicy"));
        hiddenProperties.add(new PropertyName("skin"));
        hiddenProperties.add(new PropertyName("strokeDashArray"));
        hiddenProperties.add(new PropertyName("styleableParent"));
        hiddenProperties.add(new PropertyName("tableView"));
        hiddenProperties.add(new PropertyName("tabPane"));
        hiddenProperties.add(new PropertyName("transforms"));
        hiddenProperties.add(new PropertyName("treeTableView"));
        hiddenProperties.add(new PropertyName("typeInternal"));
        hiddenProperties.add(new PropertyName("typeSelector"));
        hiddenProperties.add(new PropertyName("undoable"));
        hiddenProperties.add(new PropertyName("userData"));
        hiddenProperties.add(new PropertyName("useSystemMenuBar"));
        hiddenProperties.add(new PropertyName("valueChanging"));
        hiddenProperties.add(new PropertyName("valueConverter"));
        hiddenProperties.add(new PropertyName("valueFactory"));
        hiddenProperties.add(new PropertyName("visibleLeafColumns"));

        // Populates parentRelatedProperties
        parentRelatedProperties.add(layoutXName);
        parentRelatedProperties.add(layoutYName);
        parentRelatedProperties.add(translateXName);
        parentRelatedProperties.add(translateYName);
        parentRelatedProperties.add(translateZName);
        parentRelatedProperties.add(scaleXName);
        parentRelatedProperties.add(scaleYName);
        parentRelatedProperties.add(scaleZName);
        parentRelatedProperties.add(rotationAxisName);
        parentRelatedProperties.add(rotateName);

        // Populates sectionNames
        sectionNames.add("Properties");
        sectionNames.add("Layout");
        sectionNames.add("Code");

        // Populates subSectionMap
        final List<String> ss0 = new ArrayList<>();
        ss0.add("Custom");
        ss0.add("Text");
        ss0.add("Specific");
        ss0.add("Graphic");
        ss0.add("3D");
        ss0.add("Pagination");
        ss0.add("Stroke");
        ss0.add("Node");
        ss0.add("JavaFX CSS");
        ss0.add("Extras");
        ss0.add("Accessibility");
        subSectionMap.put("Properties", ss0);
        final List<String> ss1 = new ArrayList<>();
        ss1.add("Anchor Pane Constraints");
        ss1.add("Border Pane Constraints");
        ss1.add("Flow Pane Constraints");
        ss1.add("Grid Pane Constraints");
        ss1.add("HBox Constraints");
        ss1.add("Split Pane Constraints");
        ss1.add("Stack Pane Constraints");
        ss1.add("Tile Pane Constraints");
        ss1.add("VBox Constraints");
        ss1.add("Internal");
        ss1.add("Specific");
        ss1.add("Size");
        ss1.add("Position");
        ss1.add("Transforms");
        ss1.add("Bounds");
        ss1.add("Extras");
        ss1.add("Specific");
        subSectionMap.put("Layout", ss1);
        final List<String> ss2 = new ArrayList<>();
        ss2.add("Main");
        ss2.add("Edit");
        ss2.add("DragDrop");
        ss2.add("Closing");
        ss2.add("HideShow");
        ss2.add("Keyboard");
        ss2.add("Mouse");
        ss2.add("Rotation");
        ss2.add("Swipe");
        ss2.add("Touch");
        ss2.add("Zoom");
        subSectionMap.put("Code", ss2);
    }


    // The following properties have been rejected:
    //     javafx.embed.swing.SwingNode -> content : Property type (JComponent) is not certified
    //     javafx.scene.control.ChoiceBox -> items : Property items has no section/subSection assigned
    //     javafx.scene.control.ComboBox -> items : Property items has no section/subSection assigned
    //     javafx.scene.control.ListView -> items : Property items has no section/subSection assigned
    //     javafx.scene.control.TableColumnBase -> columns : Property is a collection but type of its items is unknown
    //     javafx.scene.control.TableView -> items : Property items has no section/subSection assigned


    // No uncertified properties have been found

}


