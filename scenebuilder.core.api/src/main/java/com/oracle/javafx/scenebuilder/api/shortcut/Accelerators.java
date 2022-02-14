/**
 * 
 */
package com.oracle.javafx.scenebuilder.api.shortcut;

import com.oracle.javafx.scenebuilder.api.action.Action;

import javafx.scene.control.MenuItem;

/**
 * @author ptreilhes
 *
 */
public interface Accelerators {

    void bind(Action action);

    void bind(Action action, MenuItem menuItem);

}