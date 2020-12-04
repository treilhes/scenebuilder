package com.oracle.javafx.scenebuilder.core.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.sun.javafx.css.CascadingStyle;
import com.sun.javafx.css.StyleManager;
import com.sun.javafx.css.StyleMap;
import com.sun.javafx.scene.NodeHelper;

import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.Style;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.scene.Node;

public class TestCssNode {

    private Node node;
    Map<String, CssMetaData<Styleable, ?>> metaMap = new HashMap<>();
    Map<CssMetaData<Styleable, ?>, CssMetaData<Styleable, ?>> parentMap = new HashMap<>();

    public TestCssNode(Node node) {
        this.node = node;
    }

    public StyleMap styleMap(Node node) {
        populateHelperMaps(node);

        Set<PseudoClass>[] triggerStates = (Set<PseudoClass>[]) new Set[0];
        return StyleManager.getInstance().findMatchingStyles(node, NodeHelper.getSubScene(node), triggerStates);
    }
    public Map<StyleableProperty<?>, List<Style>> getStyleMap(Node node) {
        StyleMap styleMap = styleMap(node);
        Map<StyleableProperty<?>, List<Style>> result = new HashMap<>();

        for (Entry<String, List<CascadingStyle>> e : styleMap.getCascadingStyles().entrySet()) {
            String k = e.getKey();
            List<CascadingStyle> v = e.getValue();

            CssMetaData<Styleable, ?> metaFinded = metaMap.get(k);

            if (metaFinded != null) {
                StyleableProperty<?> p = metaFinded.getStyleableProperty(node);

                while (p == null && metaFinded != null) {
                    metaFinded = parentMap.get(metaFinded);
                    p = metaFinded.getStyleableProperty(node);
                }

                List<Style> styles = v.stream().map(c -> c.getStyle()).collect(Collectors.toList());
                if (result.containsKey(p)) {
                    result.get(p).addAll(styles);
                } else {
                    result.put(p, styles);
                }
            }
        }
        return result;
    }

    @SuppressWarnings("rawtypes")
    public List<Style> getMatchingStyles(CssMetaData cssMetaData, Styleable styleable) {
        Node node = (Node)styleable;
        StyleMap styleMap = styleMap(node);
//        List<Style> result = new ArrayList<>();

        List<CascadingStyle> tmpList = styleMap.getCascadingStyles().get(cssMetaData.getProperty());

        return tmpList.stream().map(c -> c.getStyle()).collect(Collectors.toList());
    }

    private void populateHelperMaps(Node node) {
        if (metaMap.size() > 0) {
            return;
        }
        for (CssMetaData<? extends Styleable, ?> meta : node.getCssMetaData()) {
            metaMap.put(meta.getProperty(), (CssMetaData<Styleable, ?>) meta);
            populateWithSubMeta(metaMap, meta, parentMap);
        }
    }

    public static void populateWithSubMeta(Map<String, CssMetaData<Styleable, ?>> metaMap,
            CssMetaData<? extends Styleable, ?> meta,
            Map<CssMetaData<Styleable, ?>, CssMetaData<Styleable, ?>> parentMap) {
        if (meta.getSubProperties() != null) {
            for (CssMetaData<? extends Styleable, ?> subMeta : meta.getSubProperties()) {
                metaMap.put(subMeta.getProperty(), (CssMetaData<Styleable, ?>) subMeta);
                parentMap.put((CssMetaData<Styleable, ?>) subMeta, (CssMetaData<Styleable, ?>) meta);
                populateWithSubMeta(metaMap, subMeta, parentMap);
            }
        }
    }
}
