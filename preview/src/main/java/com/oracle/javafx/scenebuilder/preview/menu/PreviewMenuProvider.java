package com.oracle.javafx.scenebuilder.preview.menu;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.Size;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.menubar.MenuAttachment;
import com.oracle.javafx.scenebuilder.api.menubar.MenuItemProvider;
import com.oracle.javafx.scenebuilder.api.menubar.PositionRequest;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.action.editor.KeyboardModifier;
import com.oracle.javafx.scenebuilder.preview.controller.PreviewMenuController;
import com.oracle.javafx.scenebuilder.preview.controller.PreviewWindowController;

import javafx.scene.control.DialogPane;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
public class PreviewMenuProvider implements MenuItemProvider {

    private final static String PREVIEW_MENU_ID = "previewMenu";
    private final static String SHOW_PREVIEW_IN_WINDOW_ID = "showPreviewInWindow";
    private final static String SHOW_PREVIEW_IN_DIALOG_ID = "showPreviewInDialog";

    private final PreviewMenuController previewMenuController;
    private final DocumentManager documentManager;
    private final PreviewWindowController previewWindowController;
    private final Editor editor;

    public PreviewMenuProvider(@Autowired DocumentManager documentManager, @Autowired Editor editor,
            @Autowired PreviewMenuController previewMenuController,
            @Autowired PreviewWindowController previewWindowController) {
        this.documentManager = documentManager;
        this.editor = editor;
        this.previewMenuController = previewMenuController;
        this.previewWindowController = previewWindowController;
    }

    @Override
    public List<MenuAttachment> menuItems() {
        return Arrays.asList(new LaunchPreviewWindowAttachment(), new LaunchPreviewDialogAttachment(),
                MenuAttachment.separator(SHOW_PREVIEW_IN_DIALOG_ID, PositionRequest.AsNextSibling),
                new ChangePreviewSizeAttachment());
    }

    public class LaunchPreviewWindowAttachment implements MenuAttachment {

        private MenuItem menu = null;

        public LaunchPreviewWindowAttachment() {
        }

        @Override
        public String getTargetId() {
            return PREVIEW_MENU_ID;
        }

        @Override
        public PositionRequest getPositionRequest() {
            return PositionRequest.AsFirstChild;
        }

        @Override
        public MenuItem getMenuItem() {

            if (menu != null) {
                return menu;
            }

            menu = new MenuItem(I18N.getString("menu.title.show.preview.in.window"));
            menu.setId(SHOW_PREVIEW_IN_WINDOW_ID);
            menu.setAccelerator(new KeyCodeCombination(KeyCode.P, KeyboardModifier.control()));
            menu.setOnMenuValidation((e) -> menu.setDisable(editor.getFxomDocument() == null));
            menu.setOnAction((e) -> {
                previewMenuController.performOpenPreviewWindow();
            });

            return menu;
        }
    }

    public class LaunchPreviewDialogAttachment implements MenuAttachment {

        private MenuItem menu = null;

        public LaunchPreviewDialogAttachment() {
        }

        @Override
        public String getTargetId() {
            return SHOW_PREVIEW_IN_WINDOW_ID;
        }

        @Override
        public PositionRequest getPositionRequest() {
            return PositionRequest.AsNextSibling;
        }

        @Override
        public MenuItem getMenuItem() {

            if (menu != null) {
                return menu;
            }

            menu = new MenuItem(I18N.getString("menu.title.show.preview.in.dialog"));
            menu.setId(SHOW_PREVIEW_IN_DIALOG_ID);
            menu.setOnMenuValidation((e) -> menu.setDisable(editor.getFxomDocument() == null
                    || !(editor.getFxomDocument().getSceneGraphRoot() instanceof DialogPane)));
            menu.setOnAction((e) -> {
                previewMenuController.performOpenPreviewWindow();
            });

            return menu;
        }
    }

    public class ChangePreviewSizeAttachment implements MenuAttachment {

        private Menu menu = null;

        public ChangePreviewSizeAttachment() {
        }

        @Override
        public String getTargetId() {
            return PREVIEW_MENU_ID;
        }

        @Override
        public PositionRequest getPositionRequest() {
            return PositionRequest.AsLastChild;
        }

        @Override
        public MenuItem getMenuItem() {

            if (menu != null) {
                return menu;
            }

            ToggleGroup sizeToggle = new ToggleGroup();
            menu = new Menu(I18N.getString("menu.title.preview.size"));

            RadioMenuItem mi = createSizeMenu(Size.SIZE_PREFERRED, sizeToggle);
            mi.setSelected(true);

            menu.getItems().add(mi);
            menu.getItems().add(new SeparatorMenuItem());

            for (Size s : Size.values()) {
                if (s != Size.SIZE_DEFAULT && s != Size.SIZE_PREFERRED) {
                    menu.getItems().add(createSizeMenu(s, sizeToggle));
                }
            }

            menu.setOnMenuValidation((e) -> {
                mi.setText(I18N.getString("menu.title.size.preferred.with.value",
                        getStringFromDouble(previewWindowController.getRoot().prefWidth(-1)),
                        getStringFromDouble(previewWindowController.getRoot().prefHeight(-1))));
            });

            return menu;
        }

        // Returns a String with no trailing zero; if decimal part is non zero then
        // it is kept.
        private String getStringFromDouble(double value) {
            String res = Double.toString(value);
            if(res.endsWith(".0")) { //NOI18N
                res = Integer.toString((int)value);
            }
            return res;
        }

        private void updateMenuItemDisableState(MenuItem m) {
            Size size = (Size) m.getUserData();
            boolean disabledByFxom = editor.getFxomDocument() == null;
            boolean previewIsValid = previewWindowController.getStage().isShowing() && !editor.is3D() && editor.isNode()
                    && previewWindowController.sizeDoesFit(size);
            m.setDisable(disabledByFxom || !previewIsValid);
        }

        private RadioMenuItem createSizeMenu(Size size, ToggleGroup sizeToggle) {
            RadioMenuItem mi = new RadioMenuItem(size.toString());
            mi.setToggleGroup(sizeToggle);
            mi.setOnAction(e -> previewMenuController.performChangePreviewSize(size));
            mi.setUserData(size);
            mi.setOnMenuValidation((e) -> updateMenuItemDisableState(mi));
            return mi;
        }
    }
}
