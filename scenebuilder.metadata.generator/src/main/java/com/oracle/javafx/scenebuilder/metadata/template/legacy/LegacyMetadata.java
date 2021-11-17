package com.oracle.javafx.scenebuilder.metadata.template.legacy;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.oracle.javafx.scenebuilder.metadata.bean.PropertyMetaData;
import com.oracle.javafx.scenebuilder.metadata.model.Component;
import com.oracle.javafx.scenebuilder.metadata.model.Property;
import com.oracle.javafx.scenebuilder.metadata.model.Property.Type;
import com.oracle.javafx.scenebuilder.metadata.template.AbstractMetadata;

import javafx.scene.paint.Color;

public class LegacyMetadata extends AbstractMetadata {

    public static final List<String> nonAbstractButAbstract = Arrays.asList("Region", "Pane", "PopupControl",
            "MenuItem", "TextField", "ProgressIndicator", "ToggleButton", "Window");

    public static final String COMPONENT_PROPS_KEY = "componentProps";
    public static final String ALL_COMPONENTS_KEY = "allComponents";
    public static final String PROPERTY_METAS_KEY = "propertyMetas";
    public static final String PROPERTIES_KEY = "properties";
    public static final String COMPONENTS_KEY = "components";
    public static final String ABSTRACT_COMPONENTS_KEY = "abstractComponents";

    public static final String MISSING_INCLUDE_ELEMENT_FLAG = "missingIncludeElement";

    private TreeSet<Component> components = new TreeSet<>();
    private TreeSet<Component> allComponents = new TreeSet<>();
    private TreeSet<Property> properties = new TreeSet<>();
    private TreeSet<Property> propertyMetas = new TreeSet<>();
    private TreeMap<Component, TreeSet<Property>> componentProps = new TreeMap<>();

    private Map<String, Integer> componentsIndexes = new HashMap<>();
    private Map<String, Integer> propertiesIndexes = new HashMap<>();
    private Map<String, Integer> propertyMetasIndexes = new HashMap<>();
    private Map<String, Integer> allComponentsIndexes = new HashMap<>();

    private Map<String, Integer> addPropertyCompIndexes = new HashMap<>();
    private Map<String, Map<String, Integer>> addPropertyPropIndexes = new HashMap<>();

    private List<String> multiValuedProps = new ArrayList<>();

    private static final Integer DEFAULT_ORDER = 9999;

    Comparator<Component> cmpComp = Comparator
            .comparing((Component c) -> componentsIndexes.containsKey(c.getRaw().getType().getSimpleName())
                    ? componentsIndexes.get(c.getRaw().getType().getSimpleName())
                    : DEFAULT_ORDER)
            .thenComparing((Component c) -> c.getRaw().getType().getSimpleName());

    Comparator<Component> cmpCompAll = Comparator
            .comparing((Component c) -> allComponentsIndexes.containsKey(c.getRaw().getType().getSimpleName())
                    ? allComponentsIndexes.get(c.getRaw().getType().getSimpleName())
                    : DEFAULT_ORDER)
            .thenComparing((Component c) -> c.getRaw().getType().getSimpleName());

    Comparator<Property> cmpProp = Comparator
            .comparing((Property c) -> propertiesIndexes.containsKey(c.getCustom().get("memberName"))
                    ? propertiesIndexes.get(c.getCustom().get("memberName"))
                    : DEFAULT_ORDER)
            .thenComparing((Property c) -> (String) c.getCustom().get("memberName"));

    Comparator<Property> cmpPropMeta = Comparator
            .comparing((Property c) -> propertyMetasIndexes.containsKey(c.getCustom().get("metadataMemberName"))
                    ? propertyMetasIndexes.get(c.getCustom().get("metadataMemberName"))
                    : DEFAULT_ORDER)
            .thenComparing((Property c) -> (String) c.getCustom().get("metadataMemberName"));

    Comparator<Component> cmpAddCompComp = Comparator
            .comparing((Component c) -> addPropertyCompIndexes.containsKey(c.getRaw().getType().getSimpleName())
                    ? addPropertyCompIndexes.get(c.getRaw().getType().getSimpleName())
                    : DEFAULT_ORDER)
            .thenComparing((Component c) -> c.getRaw().getType().getSimpleName());

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public LegacyMetadata(Map<Component, Set<Property>> providedComponents) {

        try {
            componentsIndexes = loadMapToProp(LegacyMetadata.class.getResourceAsStream("componentsIndexes.properties"));
            propertiesIndexes = loadMapToProp(LegacyMetadata.class.getResourceAsStream("propertiesIndexes.properties"));
            propertyMetasIndexes = loadMapToProp(
                    LegacyMetadata.class.getResourceAsStream("propertyMetasIndexes.properties"));
            allComponentsIndexes = loadMapToProp(
                    LegacyMetadata.class.getResourceAsStream("allComponentsIndexes.properties"));
            addPropertyCompIndexes = loadMapToProp(
                    LegacyMetadata.class.getResourceAsStream("addPropertyCompIndexes.properties"));
            addPropertyPropIndexes = loadMapOfMapToProp(
                    LegacyMetadata.class.getResourceAsStream("addPropertyPropIndexes.properties"));

            multiValuedProps = loadList(LegacyMetadata.class.getResourceAsStream("multiValuedProps.list"));
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        components = new TreeSet<>(cmpComp);
        properties = new TreeSet<>(cmpProp);
        propertyMetas = new TreeSet<>(cmpPropMeta);
        allComponents = new TreeSet<>(cmpCompAll);
        componentProps = new TreeMap<>(cmpAddCompComp);

        put(COMPONENTS_KEY, components);
        put(PROPERTIES_KEY, properties);
        put(PROPERTY_METAS_KEY, propertyMetas);
        put(ALL_COMPONENTS_KEY, allComponents);
        put(COMPONENT_PROPS_KEY, componentProps);
        put(MISSING_INCLUDE_ELEMENT_FLAG, "xx");

        for (Map.Entry<Component, Set<Property>> e : providedComponents.entrySet()) {
            Component component = e.getKey();

            component = handleComponentTransform(component);

            Set<Property> properties = e.getValue();

            addComponent(component);

            for (Property p : properties) {
                Property property = handlePropertyTransform(component, p);
                addProperty(property);
                addPropertyMeta(component, property);
            }
        }

//		if (containsKey(MISSING_INCLUDE_ELEMENT_FLAG)) {
//			addComponent(new Component("IncludeElement", "com.oracle.javafx.scenebuilder.kit.fxom.FXOMIntrinsic", null, false, "FX"));
//		}
    }

    private Component handleComponentTransform(Component component) {

        boolean isAbstract = component.getRaw().isAbstract();
        if (nonAbstractButAbstract.contains(component.getRaw().getType().getSimpleName())) {
            isAbstract = true;
        }

        component.getCustom().put("abstract", isAbstract);
        return component;
    }

    private Property handlePropertyTransform(Component component, Property property) {
        PropertyMetaData p = property.getRaw();

        String newMemberName = p.getName();
        String newMetadataMemberName = p.getName();

        newMemberName = p.isStatic() ? p.getResidenceClass().getSimpleName() + "_" + newMemberName : newMemberName;

        if (property.getType() == Type.COMPONENT) {
            try {

                String overidedName = componentMetaNameOverride(component, property);

                if (overidedName != null) {
                    newMetadataMemberName = overidedName;
                } else if (multiValuedProps.contains(p.getName())) { // if multivalued, insert value in name
                    newMetadataMemberName = p.getName() + "_" + p.getContentType().getSimpleName() + "_";
                }

                property.getCustom().put("memberName", newMemberName);
                property.getCustom().put("metadataMemberName", newMetadataMemberName);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (property.getType() == Type.VALUE) {
            try {

                Object def = p.getDefaultValue();
                String defaultValue = computeStringValue(component, property);
                String nullEquivalent = null;

                if (p.getType().isEnum() && def != null && defaultValue == null) {
                    nullEquivalent = def.toString();
                }

                String overidedName = propertyMetaNameOverride(component, property);

                if (overidedName != null) {
                    newMetadataMemberName = overidedName;
                } else if (!p.isStatic() && multiValuedProps.contains(p.getName())) { // if multivalued, insert value in
                                                                                      // name

                    String inserted = (def == null ? "NULL" : def.toString());

                    try {
                        double val = Double.parseDouble(inserted);
                        if (val == -1.0) {
                            inserted = "COMPUTED";
                        } else {
                            inserted = ((int) (val * 100)) + "";
                        }
                    } catch (NumberFormatException e) {
                    }

                    newMetadataMemberName = p.getName() + "_" + inserted + "_";
                } else if (p.isStatic()) {
                    newMetadataMemberName = newMemberName;
                }

//				p = new Property(p.getRaw(),p.getName(),newMemberName ,newMetadataMemberName, p.getResidenceClass(), p.getPropertyMetadataClass(),
//						p.getSection(), p.getSubsection(), p.getSubsectionindex(),
//						p.isRw(), defaultValue, nullEquivalent, p.getEnumCls(), p.isStatic(), p.getKind(), p.isCollection(), p.getCollectionType());

                property.getCustom().put("memberName", newMemberName);
                property.getCustom().put("metadataMemberName", newMetadataMemberName);
                property.getCustom().put("defaultValue", defaultValue);
                property.getCustom().put("nullEquivalent", nullEquivalent);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return property;
    }

    private static String propertyMetaNameOverride(Component component, Property property) throws Exception {
        // format: "%s_%s_";

        String propertyName = property.getRaw().getName();
        String className = component.getRaw().getType().getSimpleName();
        Object value = property.getRaw().getDefaultValue();
        boolean readWrite = property.getRaw().isReadWrite();

        if ("columnResizePolicy".equals(propertyName)) {
            if (className.equals("TableView")) {
                return "columnResizePolicy_TABLEVIEW_UNCONSTRAINED_";
            }
            if (className.equals("TreeTableView")) {
                return "columnResizePolicy_TREETABLEVIEW_UNCONSTRAINED_";
            }
        }

        if ("type".equals(propertyName)) {
            if (className.equals("BottomNavigation")) {
                return "bottomNavigationType";
            }
            if (className.equals("Arc")) {
                return "type";
            }
        }

        if ("fill".equals(propertyName)) {
            if (className.equals("Shape")) {
                return "fill_BLACK_";
            }
            if (className.equals("Scene")) {
                return "fill_WHITE_";
            }
        }

        if ("halignment".equals(propertyName)) {
            if (className.equals("ColumnConstraints")) {
                return "halignment_NULL_";
            }
        }

        if ("valignment".equals(propertyName)) {
            if (className.equals("RowConstraints")) {
                return "valignment_NULL_";
            }
        }

        if ("height".equals(propertyName)) {
            if (className.equals("Window")) {
                return "height_Double_COMPUTED_";
            }
            if (!readWrite) {
                return "height_Double_ro_";
            }
            if (Double.valueOf(0.0).equals(value)) {
                return "height_Double_0_";
            }
            if (Double.valueOf(2.0).equals(value)) {
                return "height_Double_200_";
            }
        }

        if ("indeterminate".equals(propertyName)) {
            if (!readWrite) {
                return "indeterminate_Boolean_ro_";
            } else {
                return "indeterminate_Boolean_";
            }
        }

        if ("length".equals(propertyName)) {
            if (!readWrite) {
                return "length_Integer_ro_";
            } else {
                return "length_Double_";
            }
        }

        if ("resizable".equals(propertyName)) {
            if (!readWrite) {
                return "resizable_Boolean_ro_";
            } else {
                return "resizable_Boolean_";
            }
        }

        if ("selected".equals(propertyName)) {
            if (!readWrite) {
                return "selected_Boolean_ro_";
            } else {
                return "selected_Boolean_";
            }
        }

        if ("maxHeight".equals(propertyName)) {
            if (className.equals("Stage")) {
                return "maxHeight_SIZE_";
            }

            if (className.equals("WebView")) {
                return "maxHeight_MAX_";
            }
        }

        if ("minHeight".equals(propertyName)) {
            if (className.equals("Stage")) {
                return "minHeight_SIZE_";
            }
//			if (className.equals("WebView")) {
//				return "maxHeight_MAX_";
//			}
        }

        if ("minWidth".equals(propertyName)) {
            if (className.equals("Stage")) {
                return "minWidth_SIZE_";
            }
//			if (className.equals("WebView")) {
//				return "maxHeight_MAX_";
//			}
        }

        if ("maxWidth".equals(propertyName)) {
            if (className.equals("Stage")) {
                return "maxWidth_SIZE_";
            }
            if (className.equals("WebView")) {
                return "maxWidth_MAX_";
            }
        }

        if ("minorTickCount".equals(propertyName)) {
            if (className.equals("Slider")) {
                return "minorTickCount_3_";
            }
            if (className.equals("ValueAxis")) {
                return "minorTickCount_5_";
            }
        }

        if ("prefColumnCount".equals(propertyName)) {
            if (className.equals("TextArea")) {
                return "prefColumnCount_40_";
            }
            if (className.equals("TextField")) {
                return "prefColumnCount_12_";
            }
        }

        if ("side".equals(propertyName)) {
            if (className.equals("Axis")) {
                return "side_NULL_";
            }
            if (className.equals("TabPane")) {
                return "side_TOP_";
            }
        }

        if ("sortType".equals(propertyName)) {
            return "sortType_SortType_";
        }

        if ("stroke".equals(propertyName)) {
            if (value != null) {
                return "stroke_BLACK_";
            }
        }

        if ("content".equals(propertyName)) {
            if (className.equals("SVGPath")) {
                return "content_String_";
            }
        }

        if ("value".equals(propertyName)) {
            if (!readWrite) {
                return "value_Object_ro_";
            }
            if (value == null) {
                return "value_Object_";
            }
            if (value instanceof Color) {
                return "value_Color_";
            }
            if (value instanceof Double) {
                return "value_Double_";
            }
        }

        if ("width".equals(propertyName)) {
            if (!readWrite) {
                return "width_Double_ro_";
            }
            if (Double.valueOf(2.0).equals(value)) {
                return "width_Double_200_";
            }
            if (Double.valueOf(0.0).equals(value)) {
                return "width_Double_0_";
            }
            if (Double.valueOf(-1.0).equals(value)) {
                return "width_Double_COMPUTED_";
            }

        }

        if ("x".equals(propertyName)) {
            if (value != null && Double.isNaN((double) value)) {
                return "x_NaN_";
            } else {
                return "x_0_";
            }
        }

        if ("y".equals(propertyName)) {
            if (value != null && Double.isNaN((double) value)) {
                return "y_NaN_";
            } else {
                return "y_0_";
            }
        }

        if ("source".equals(propertyName)) {
            return "includeFxml";
        }

        if ("expanded".equals(propertyName)) {
//			if (vpm.getInspectorPath().getSubSectionIndex() == 0) {
//				return "expanded";
//			}
            if (className.equals("DialogPane")) {
                return "expanded_false_";
            }
            if (className.equals("ExpansionPanel")) {
                return "expanded";
            }
        }

        if ("styleClass".equals(propertyName)) {
            if (className.equals("Accordion")) {
                return "styleClass_c4_";
            }
            if (className.equals("AreaChart")) {
                return "styleClass_c37_";
            }
            if (className.equals("Axis")) {
                return "styleClass_c45_";
            }
            if (className.equals("BarChart")) {
                return "styleClass_c1_";
            }
            if (className.equals("BubbleChart")) {
                return "styleClass_c37_";
            }
            if (className.equals("Button")) {
                return "styleClass_c17_";
            }
            if (className.equals("ButtonBar")) {
                return "styleClass_c35_";
            }
            if (className.equals("ButtonBase")) {
                return "styleClass_c41_";
            }
            if (className.equals("CategoryAxis")) {
                return "styleClass_c45_";
            }
            if (className.equals("Chart")) {
                return "styleClass_c37_";
            }
            if (className.equals("CheckBox")) {
                return "styleClass_c10_";
            }
            if (className.equals("CheckMenuItem")) {
                return "styleClass_c28_";
            }
            if (className.equals("ChoiceBox")) {
                return "styleClass_c43_";
            }
            if (className.equals("ColorPicker")) {
                return "styleClass_c5_";
            }
            if (className.equals("ComboBox")) {
                return "styleClass_c11_";
            }
            if (className.equals("ComboBoxBase")) {
                return "styleClass_c5_";
            }
            if (className.equals("ContextMenu")) {
                return "styleClass_c8_";
            }
            if (className.equals("Control")) {
                return "styleClass_c25_";
            }
            if (className.equals("CustomMenuItem")) {
                return "styleClass_c27_";
            }
            if (className.equals("DatePicker")) {
                return "styleClass_c9_";
            }
            if (className.equals("DialogPane")) {
                return "styleClass_c30_";
            }
            if (className.equals("HTMLEditor")) {
                return "styleClass_c21_";
            }
            if (className.equals("Hyperlink")) {
                return "styleClass_c25_";
            }
            if (className.equals("ImageView")) {
                return "styleClass_c20_";
            }
            if (className.equals("Label")) {
                return "styleClass_c3_";
            }
            if (className.equals("Labeled")) {
                return "styleClass_c41_";
            }
            if (className.equals("LineChart")) {
                return "styleClass_c37_";
            }
            if (className.equals("ListView")) {
                return "styleClass_c34_";
            }
            if (className.equals("MediaView")) {
                return "styleClass_c46_";
            }
            if (className.equals("Menu")) {
                return "styleClass_c29_";
            }
            if (className.equals("MenuBar")) {
                return "styleClass_c18_";
            }
            if (className.equals("MenuButton")) {
                return "styleClass_c52_";
            }
            if (className.equals("MenuItem")) {
                return "styleClass_c36_";
            }
            if (className.equals("Node")) {
                return "styleClass_empty_";
            }
            if (className.equals("NumberAxis")) {
                return "styleClass_c45_";
            }
            if (className.equals("Pagination")) {
                return "styleClass_c39_";
            }
            if (className.equals("PasswordField")) {
                return "styleClass_c53_";
            }
            if (className.equals("PieChart")) {
                return "styleClass_c37_";
            }
            if (className.equals("PopupControl")) {
                return "styleClass_empty_";
            }
            if (className.equals("ProgressBar")) {
                return "styleClass_c13_";
            }
            if (className.equals("ProgressIndicator")) {
                return "styleClass_c50_";
            }
            if (className.equals("RadioButton")) {
                return "styleClass_c41_";
            }
            if (className.equals("RadioMenuItem")) {
                return "styleClass_c7_";
            }
            if (className.equals("ScatterChart")) {
                return "styleClass_c37_";
            }
            if (className.equals("ScrollBar")) {
                return "styleClass_c33_";
            }
            if (className.equals("ScrollPane")) {
                return "styleClass_c38_";
            }
            if (className.equals("Separator")) {
                return "styleClass_c31_";
            }
            if (className.equals("SeparatorMenuItem")) {
                return "styleClass_c23_";
            }
            if (className.equals("Slider")) {
                return "styleClass_c40_";
            }
            if (className.equals("Spinner")) {
                return "styleClass_c24_";
            }
            if (className.equals("SplitMenuButton")) {
                return "styleClass_c2_";
            }
            if (className.equals("SplitPane")) {
                return "styleClass_c14_";
            }
            if (className.equals("StackedAreaChart")) {
                return "styleClass_c37_";
            }
            if (className.equals("StackedBarChart")) {
                return "styleClass_c12_";
            }
            if (className.equals("Tab")) {
                return "styleClass_c19_";
            }
            if (className.equals("TabPane")) {
                return "styleClass_c6_";
            }
            if (className.equals("TableColumnBase")) {
                return "styleClass_c42_";
            }
            if (className.equals("TableView")) {
                return "styleClass_c49_";
            }
            if (className.equals("TextArea")) {
                return "styleClass_c51_";
            }
            if (className.equals("TextField")) {
                return "styleClass_c47_";
            }
            if (className.equals("TextInputControl")) {
                return "styleClass_c51_";
            }
            if (className.equals("TitledPane")) {
                return "styleClass_c26_";
            }
            if (className.equals("ToggleButton")) {
                return "styleClass_c44_";
            }
            if (className.equals("ToolBar")) {
                return "styleClass_c16_";
            }
            if (className.equals("Tooltip")) {
                return "styleClass_c15_";
            }
            if (className.equals("TreeTableView")) {
                return "styleClass_c32_";
            }
            if (className.equals("TreeView")) {
                return "styleClass_c22_";
            }
            if (className.equals("ValueAxis")) {
                return "styleClass_c45_";
            }
            if (className.equals("WebView")) {
                return "styleClass_c48_";
            }
            if (className.equals("XYChart")) {
                return "styleClass_c37_";
            }

            return "styleClass_" + className + "_";
        }
        return null;
    }

    private static String componentMetaNameOverride(Component component, Property property) throws Exception {
        // format: "%s_%s_";

        String propertyName = property.getRaw().getName();
        String className = component.getRaw().getType().getSimpleName();

        if ("actionItems".equals(propertyName)) {
            return "actionItems_Node_";
        }

        if ("buttons".equals(propertyName)) {
            if (className.equals("ExpandedPanel")) {
                return "buttons_EXPANDEDPANEL_";
            }
            if (className.equals("ButtonBar")) {
                return "buttons";
            }
        }

        if ("children".equals(propertyName)) {
            if (className.equals("DialogPane")) {
                return "children_c1_";
            } else {
                return "children_empty_";
            }
        }

        if ("content".equals(propertyName)) {
            if (className.equals("ExpandedPanel")) {
                return "content_EXPANDEDPANEL_";
            } else if (className.equals("SeparatorMenuItem")) {
                return "content_Node_SEPARATOR_";
            } else {
                return "content_Node_NULL_";
            }
        }

        if ("root".equals(propertyName)) {
            return "root_scene_";
        }

        if ("scene".equals(propertyName)) {
            return "scene_stage_";
        }

        if ("toggles".equals(propertyName)) {
            return "toggles_ToggleButton_";
        }

        if ("options".equals(propertyName)) {
            return "options_Option_";
        }

        if ("titleNodes".equals(propertyName)) {
            return "titleNodes_Node_";
        }

        return null;
    }

    public void addComponent(Component component) {
        components.add(component);
        allComponents.add(component);
    }

    public void addProperty(Property property) {
        properties.add(property);
    }

    public void addPropertyMeta(Component component, Property property) {
        try {
            propertyMetas.add(property);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        TreeSet<Property> tmpPropertyMetas = componentProps.get(component);

        final String cmpName = component.getRaw().getType().getSimpleName();
        if (tmpPropertyMetas == null) {
            tmpPropertyMetas = new TreeSet<>(Comparator.comparing((Property c) -> {
                if (!addPropertyPropIndexes.containsKey(cmpName)) {
                    return DEFAULT_ORDER;
                }
                return addPropertyPropIndexes.get(cmpName).containsKey(c.getCustom().get("metadataMemberName"))
                        ? addPropertyPropIndexes.get(cmpName).get(c.getCustom().get("metadataMemberName"))
                        : DEFAULT_ORDER;
            }).thenComparing((Property c) -> (String) c.getCustom().get("metadataMemberName")));
            componentProps.put(component, tmpPropertyMetas);
        }

        tmpPropertyMetas.add(property);
    }

    private Map<String, Integer> loadMapToProp(InputStream target) {
        Map<String, Integer> map = new HashMap<>();
        Properties prop = new Properties();
        try (InputStream fos = target) {
            prop.load(fos);
            prop.entrySet().forEach(e -> map.put(e.getKey().toString(), Integer.parseInt(e.getValue().toString())));
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return map;
    }

    private Map<String, Map<String, Integer>> loadMapOfMapToProp(InputStream target) {
        Map<String, Map<String, Integer>> map = new HashMap<>();
        Properties prop = new Properties();
        try (InputStream fos = target) {
            prop.load(fos);
            prop.entrySet().forEach(e -> {
                String key = e.getKey().toString();
                String value = e.getValue().toString();
                String key0 = key.substring(0, key.indexOf("_"));
                String key1 = key.substring(key.indexOf("_") + 1, key.length());
                map.computeIfAbsent(key0, k -> new HashMap<>()).put(key1, Integer.parseInt(value));
            });
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return map;
    }

    private List<String> loadList(InputStream target) {
        List<String> list = new ArrayList<>();
        try (BufferedReader fos = new BufferedReader(new InputStreamReader(target))) {
            String line = null;
            while ((line = fos.readLine()) != null) {
                list.add(line.trim());
            }

        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return list;
    }
}
