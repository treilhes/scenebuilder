package com.oracle.javafx.scenebuilder.util;

import javafx.scene.Node;

public class NodeUtils {
    private NodeUtils() {}
    
    public static boolean isDescendantOf(Node container, Node node) {
        Node child = node;
        while (child != null) {
            if (child == container) {
                return true;
            }
            child = child.getParent();
        }
        return false;
    }
}
