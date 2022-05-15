/**
 * 
 */
package com.oracle.javafx.scenebuilder.api.dock;

import java.util.Collection;
import java.util.UUID;

/**
 *
 */
public interface DockViewController {

    void performResetDockAndViews();

    void performLoadDockAndViewsPreferences();

    Collection<ViewAttachment> getViewItems();

    void performOpenView(View view);

    void performOpenView(ViewAttachment vi);

    void performCloseView(View view);

    void performUndock(View view);

    void performDock(View view, UUID targetDockId);

    Dock getDock(UUID dockId);

}