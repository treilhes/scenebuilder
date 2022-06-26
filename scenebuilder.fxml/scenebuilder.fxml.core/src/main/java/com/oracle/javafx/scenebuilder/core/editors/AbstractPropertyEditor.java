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
package com.oracle.javafx.scenebuilder.core.editors;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.scenebuilder.fxml.api.Documentation;

import com.oracle.javafx.scenebuilder.api.Dialog;
import com.oracle.javafx.scenebuilder.api.css.CssPropAuthorInfo;
import com.oracle.javafx.scenebuilder.api.editor.panel.util.dialog.Alert;
import com.oracle.javafx.scenebuilder.api.editor.panel.util.dialog.Alert.ButtonID;
import com.oracle.javafx.scenebuilder.api.editor.selection.SelectionState;
import com.oracle.javafx.scenebuilder.api.fs.FileSystem;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.util.EditorUtils;

import javafx.animation.FadeTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.Control;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.util.Duration;

/**
 * Base class for all property editors.
 *
 *
 */
public abstract class AbstractPropertyEditor extends AbstractEditor {

    // Layout format for editors. See DTL-5727 for details.
    public enum LayoutFormat {

        SIMPLE_LINE_CENTERED,
        SIMPLE_LINE_BOTTOM,
        SIMPLE_LINE_TOP,
        SIMPLE_LINE_NO_NAME,
        DOUBLE_LINE
    }
    public final static LayoutFormat DEFAULT_LAYOUT_FORMAT = LayoutFormat.SIMPLE_LINE_CENTERED;
    private static final Image cssIcon = new Image(
            AbstractPropertyEditor.class.getResource("css-icon.png").toExternalForm());
    private Hyperlink propName;
    private HBox propNameNode;
    private MenuButton menu;
    private ValuePropertyMetadata propMeta = null;
    private Object defaultValue;
    private final Set<ChangeListener<Object>> valueListeners = new HashSet<>();
    private final Set<ChangeListener<Object>> transientValueListeners = new HashSet<>();
    private final Set<ChangeListener<Boolean>> editingListeners = new HashSet<>();
    private ChangeListener<String> navigateRequestListener = null;
    private EventHandler<?> commitListener;
    // State properties
    private final BooleanProperty disableProperty = new SimpleBooleanProperty(false);
    private boolean binding = false;
    private final BooleanProperty indeterminateProperty = new SimpleBooleanProperty(false);
    private boolean ruledByCss = false;
    private CssPropAuthorInfo cssInfo;
    private MenuItem showCssMenuItem = null;
    private boolean updateFromModel = true; // Value update from the model
    private final ObjectProperty<Object> valueProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<Object> transientValueProperty = new SimpleObjectProperty<>();
    private final BooleanProperty editingProperty = new SimpleBooleanProperty(false);
    private final BooleanProperty invalidValueProperty = new SimpleBooleanProperty(false);
    private final StringProperty navigateRequestProperty = new SimpleStringProperty();
    private boolean handlingError = false;
    private LayoutFormat layoutFormat = DEFAULT_LAYOUT_FORMAT;

    private final MenuItem resetvalueMenuItem = new MenuItem(I18N.getString("inspector.editors.resetvalue"));
    private FadeTransition fadeTransition = null;
    private boolean genericModesHandled = false;

    private Set<Class<?>> selectedClasses;

    private final Documentation documentation;
    private final FileSystem fileSystem;
    private final Dialog dialog;


    public AbstractPropertyEditor(
            Dialog dialog,
            Documentation documentation,
            FileSystem fileSystem
            ) {
        this.dialog = dialog;
        this.documentation = documentation;
        this.fileSystem = fileSystem;
        initialize();
    }

    private void initialize() {
        // Create a property link with a pretty name (e.g. layoutX ==> Layout X)
        propName = new Hyperlink();
        propName.setOnAction(event -> documentation.openDocumentationUrl(selectedClasses, propMeta));
        propName.getStyleClass().add("property-link"); //NOCHECK
        propName.setFocusTraversable(false);

        // The hyperlink is wrapped in an HBox so that the HBox grows in width, not the hyperlink
        propNameNode = new HBox();
        propNameNode.getChildren().add(propName);
        // default layout: simple line, centered vertically, propName aligned on right
        propNameNode.setAlignment(Pos.CENTER_RIGHT);

        EditorUtils.makeWidthStretchable(propNameNode);
    }

    public HBox getPropNameNode() {
        return propNameNode;
    }

    @Override
    public PropertyName getPropertyName() {
        if (propMeta == null) {
            return null;
        }
        return propMeta.getName();
    }

    @Override
    public String getPropertyNameText() {
        return propName.getText();
    }

    public void setPropertyText(String text) {
        propName.setText(text);
    }

    @Override
    public final MenuButton getMenu() {
        if (menu == null) {
            menu = new MenuButton();

            Region region = new Region();
            menu.setGraphic(region);
            region.getStyleClass().add("cog-shape"); //NOCHECK

            menu.disableProperty().bind(disableProperty);
            menu.getStyleClass().add("cog-menubutton"); //NOCHECK
            menu.setOpacity(0);
            if (fadeTransition == null) {
                fadeTransition = new FadeTransition(Duration.millis(500), menu);
            }
            EditorUtils.handleFading(fadeTransition, menu, disableProperty);
            EditorUtils.handleFading(fadeTransition, propNameNode, disableProperty);
            menu.focusedProperty().addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
                if (newValue) {
                    // focused
                    EditorUtils.fadeTo(fadeTransition, 1);
                } else {
                    // focus lost
                    EditorUtils.fadeTo(fadeTransition, 0);
                }
            });
            menu.getItems().add(resetvalueMenuItem);
            resetvalueMenuItem.setOnAction(e -> {
                setValue(defaultValue);
                userUpdateValueProperty(defaultValue);
            });
        }
        return menu;
    }

    public void replaceMenuItem(MenuItem item, MenuItem newItem) {
        MenuButton cogMenu = getMenu();
        int index = cogMenu.getItems().indexOf(item);

        if (cogMenu.getItems().contains(newItem) && index == -1) {
            // change is already done
            return;
        }

        assert index != -1;
        cogMenu.getItems().set(index, newItem);
    }

    public void setPropertyMetadata(ValuePropertyMetadata propMeta) {
        this.propMeta = propMeta;
    }

    @Override
    public void addValueListener(ChangeListener<Object> listener) {
        if (!valueListeners.contains(listener)) {
            valueProperty().addListener(listener);
            valueListeners.add(listener);
        }
    }

    public void removeValueListener(ChangeListener<Object> listener) {
        valueProperty().removeListener(listener);
        valueListeners.remove(listener);
    }

    @Override
    public void addTransientValueListener(ChangeListener<Object> listener) {
        if (!transientValueListeners.contains(listener)) {
            transientValueProperty().addListener(listener);
            transientValueListeners.add(listener);
        }
    }

    public void removeTransientValueListener(ChangeListener<Object> listener) {
        transientValueProperty().removeListener(listener);
        transientValueListeners.remove(listener);
    }

    @Override
    public void addEditingListener(ChangeListener<Boolean> listener) {
        if (!editingListeners.contains(listener)) {
            editingProperty().addListener(listener);
            editingListeners.add(listener);
        }
    }

    public void removeEditingListener(ChangeListener<Boolean> listener) {
        editingProperty().removeListener(listener);
        editingListeners.remove(listener);
    }

    @Override
    public void addNavigateListener(ChangeListener<String> listener) {
        // We should have a single listener here
        if (navigateRequestListener == null) {
            navigateRequestProperty.addListener(listener);
            navigateRequestListener = listener;
        }
    }

    public void removeNavigateListener(ChangeListener<String> listener) {
        navigateRequestProperty.removeListener(listener);
        navigateRequestListener = null;
    }

    @Override
    public void removeAllListeners() {
        Set<ChangeListener<Object>> valListeners = new HashSet<>(valueListeners);
        for (ChangeListener<Object> listener : valListeners) {
            removeValueListener(listener);
        }
        Set<ChangeListener<Object>> transientValListeners = new HashSet<>(transientValueListeners);
        for (ChangeListener<Object> listener : transientValListeners) {
            removeTransientValueListener(listener);
        }
        Set<ChangeListener<Boolean>> editListeners = new HashSet<>(editingListeners);
        for (ChangeListener<Boolean> listener : editListeners) {
            removeEditingListener(listener);
        }
        removeNavigateListener(navigateRequestListener);
    }

    /*
     * Abstract methods
     */
    public abstract Object getValue();

    @Override
    public abstract void setValue(Object value);

    protected abstract void valueIsIndeterminate();

    @Override
    public abstract void requestFocus();

    public void setValueGeneric(Object value) {
        // Should be called (first line) from editors setValue()
//        System.out.println(getPropertyNameText() + " - setValue() to : " + value);
        if (!isUpdateFromModel()) {
            // User updated the value from this editor: nothing to do.
            return;
        }
        invalidValueProperty.setValue(false);
        valueProperty.setValue(value);
        resetMenuUpdate(value);
//        cssMenuUpdate();
        if (isRuledByCss()) {
            addCssVisual();
        } else {
            removeCssVisual();
        }
        if (!(value instanceof String)) {
            return;
        }
        String val = (String) value;

        // Handle generic binding case
        if (isBindingExpression(val)) {
            binding = true;
        }
    }

    private void resetMenuUpdate(Object value) {
        // "Reset value" menu item update
        if (value == null) {
            if (defaultValue == null) {
                resetvalueMenuItem.setDisable(true);
            }
        } else if (value.equals(defaultValue)) {
            resetvalueMenuItem.setDisable(true);
        } else {
            resetvalueMenuItem.setDisable(false);
        }
    }

    private void cssMenuUpdate() {
        // "Show css" menu item update
        if (!isRuledByCss()) {
            getMenu().getItems().remove(showCssMenuItem);
            showCssMenuItem = null;
        }
    }

    protected boolean isSetValueDone() {
        boolean done = !isHandlingError() && (isBinding() || isEditing());
        return done;
    }

    /*
     * State properties
     *
     */
    public boolean isDisabled() {
        return disableProperty.getValue();
    }

    @Override
    public void setDisable(boolean disabled) {
        disableProperty.setValue(disabled);
    }

    public ObservableBooleanValue disableProperty() {
        return disableProperty;
    }

    @Override
    public boolean isDisablePropertyBound(){
        return getValueEditor().disableProperty().isBound();
    }

    @Override
    public void unbindDisableProperty(){
        getValueEditor().disableProperty().unbind();
    }

    public boolean isBinding() {
        return binding;
    }

    public boolean isIndeterminate() {
        return indeterminateProperty.getValue();
    }

    @Override
    public void setIndeterminate(boolean indeterminate) {
//        System.out.println(propName.getText() + " : setIndeterminate() to " + indeterminate);
        if (!indeterminateProperty.getValue() && indeterminate) {
            valueIsIndeterminate();
        }
        indeterminateProperty.setValue(indeterminate);
    }

    @Override
    public boolean isRuledByCss() {
        return ruledByCss;
    }

    @Override
    public void setRuledByCss(boolean ruledByCss) {
        this.ruledByCss = ruledByCss;
    }

    @Override
    public void setCssInfo(CssPropAuthorInfo cssInfo) {
        this.cssInfo = cssInfo;
    }

    @Override
    public boolean isUpdateFromModel() {
        return updateFromModel;
    }

    @Override
    public void setUpdateFromModel(boolean updateFromModel) {
        this.updateFromModel = updateFromModel;
    }

    public boolean isEditing() {
        return editingProperty.getValue();
    }

    @Override
    public boolean isInvalidValue() {
        return invalidValueProperty.getValue();
    }

    public boolean isHandlingError() {
        return handlingError;
    }

    // Reset everything so that the editor can be re-used for another property
    @Override
    public void reset(ValuePropertyMetadata propMeta, SelectionState selectionState) {
        resetStates();
        this.propMeta = propMeta;
        setSelectedClasses(selectionState == null ? new HashSet<>() : selectionState.getSelectedClasses());
        setPropNamePrettyText();
        this.defaultValue = propMeta.getDefaultValueObject();
    }

    protected void reset(String name, String defaultValue) {
        resetStates();
        propName.setText(name);
        this.defaultValue = defaultValue;
    }

    public ObjectProperty<Object> valueProperty() {
        return valueProperty;
    }

    public ObjectProperty<Object> transientValueProperty() {
        return transientValueProperty;
    }

    public LayoutFormat getLayoutFormat() {
        return layoutFormat;
    }

    @Override
    public void setLayoutFormat(LayoutFormat layoutFormat) {
        this.layoutFormat = layoutFormat;
    }

    public void userUpdateValueProperty(Object value) {
        userUpdateValueProperty(value, false);
    }

    public void userUpdateTransientValueProperty(Object value) {
        userUpdateValueProperty(value, true);
    }

    private void userUpdateValueProperty(Object value, boolean transientValue) {
        if (!transientValue && !isValueChanged(value)) {
            return;
        }
        invalidValueProperty.setValue(false);
        indeterminateProperty.setValue(false);
        if (transientValue) {
            transientValueProperty.setValue(value);
        } else {
            valueProperty.setValue(value);
        }
        resetMenuUpdate(value);
    }

    @SuppressWarnings("unchecked")
    protected boolean isValueChanged(Object value) {
        if (value == null || valueProperty.getValue() == null) {
            return value != valueProperty.getValue();
        }
        if (value instanceof List) {
            List<Object> valueList = (List<Object>) value;
            List<Object> valuePropertyList = (List<Object>) valueProperty.getValue();
            return isIndeterminate() || !Objects.equals(valueList, valuePropertyList);
        } else {
            return isIndeterminate() || !Objects.equals(value, valueProperty.getValue());
        }
    }

    public BooleanProperty editingProperty() {
        return editingProperty;
    }

    public BooleanProperty indeterminateProperty() {
        return indeterminateProperty;
    }

    public BooleanProperty invalidValueProperty() {
        return invalidValueProperty;
    }

    public StringProperty navigateRequestProperty() {
        return navigateRequestProperty;
    }

    protected static Node getBindingValueEditor(Node valueEditor, String bindingExp) {
        TextField bindingTf = new TextField();
        bindingTf.setText(bindingExp);
        bindingTf.setEditable(false);
//                bindingTf.getStyleClass().add("read-only"); //NOCHECK
        HBox hbox = new HBox(5);
        EditorUtils.replaceNode(valueEditor, hbox, null);
        hbox.getChildren().addAll(new Label("${"), bindingTf, new Label("}")); //NOCHECK
        return hbox;
    }

    protected static boolean isBindingExpression(String str) {
        return str.startsWith("${") && str.endsWith("}"); //NOCHECK
    }

    private void addCssVisual() {
        if (!propNameNode.getStyleClass().contains("css-override")) { //NOCHECK
            ImageView iv = new ImageView(cssIcon);
            propName.setGraphic(iv);
            propNameNode.getStyleClass().add("css-override"); //NOCHECK

            // menu
            if (showCssMenuItem == null) {
                showCssMenuItem = new MenuItem(I18N.getString("inspector.css.showcss"));
                showCssMenuItem.setOnAction(e -> {
                    assert cssInfo != null;
                    if (cssInfo.isInline()) {
                        // Jump to the "style" property
                        navigateRequestProperty.setValue("style"); //NOCHECK
                        navigateRequestProperty.setValue(null);
                    } else {
                        // Open the css file
                        if (cssInfo.getMainUrl() != null) {
                        	try {
                        	    fileSystem.open(cssInfo.getMainUrl().toString());
                            } catch (IOException ex) {
                                System.out.println(ex.getMessage() + ex);
                            }
                        }
                    }
                });
            }
            getMenu().getItems().add(showCssMenuItem);
        }
    }

    private void removeCssVisual() {
        if (propNameNode.getStyleClass().contains("css-override")) { //NOCHECK
            propName.setGraphic(null);
            propNameNode.getStyleClass().remove("css-override"); //NOCHECK
        }
        cssMenuUpdate();
    }

    protected Node handleGenericModes(Node valueEditor) {
        if (!genericModesHandled) {
            if (isBinding()) {
                assert getValue() instanceof String;
                return getBindingValueEditor(valueEditor, (String) getValue());
            }
            if (isRuledByCss()) {
                addCssVisual();
            } else {
                removeCssVisual();
            }
            if (fadeTransition == null) {
                fadeTransition = new FadeTransition(Duration.millis(500), getMenu());
            }
            EditorUtils.handleFading(fadeTransition, valueEditor, disableProperty);
            genericModesHandled = true;
        }
        return valueEditor;
    }

    @Override
    public ValuePropertyMetadata getPropertyMeta() {
        return propMeta;
    }

    protected void handleInvalidValue(Object value) {
        handleInvalidValue(value, null);
    }

    protected void handleInvalidValue(Object value, Node source) {
        if (isHandlingError()) {
            return;
        }
        invalidValueProperty.setValue(true);
        handlingError = true;
        if (source == null) {
            source = propName;
        }

        Alert alertDialog = dialog.customAlert(source.getScene().getWindow());
        alertDialog.setTitle(I18N.getString("inspector.error.title"));
        alertDialog.setMessage(I18N.getString("inspector.error.message"));
        alertDialog.setDetails(I18N.getString("inspector.error.details", value, getPropertyNameText()));
        // OK button is "Previous value"
        alertDialog.setOKButtonVisible(true);
        alertDialog.setOKButtonTitle(I18N.getString("inspector.error.previousvalue"));
        // Cancel button
        alertDialog.setDefaultButtonID(ButtonID.CANCEL);
        alertDialog.setShowDefaultButton(true);
        alertDialog.setCancelButtonTitle(I18N.getString("inspector.error.cancel"));

        ButtonID buttonClicked = alertDialog.showAndWait();
        if (buttonClicked == ButtonID.OK) {
            setValue(valueProperty().getValue());
            invalidValueProperty.setValue(false);
        }
        //TODO check if it close
        //alertDialog.getStage().close();

        // Get the focus back
        requestFocus();
        handlingError = false;
    }

    private void resetStates() {
        // State properties
        disableProperty.setValue(false);
        binding = false;
        indeterminateProperty.setValue(false);
        ruledByCss = false;
        updateFromModel = true;
        editingProperty.setValue(false);
        invalidValueProperty.setValue(false);

        genericModesHandled = false;
        layoutFormat = DEFAULT_LAYOUT_FORMAT;
        cssInfo = null;
        removeCssVisual();
    }

    private void setSelectedClasses(Set<Class<?>> selClasses) {
        this.selectedClasses = selClasses;
        if (selClasses == null) {
            return;
        }
        if (selClasses.size() > 1) {
            // multi-selection of different classes ==> no link
            propName.setMouseTransparent(true);
        } else {
            propName.setMouseTransparent(false);
        }
    }

    private void setPropNamePrettyText() {
        propName.setText(EditorUtils.toDisplayName(getPropertyName().getName()));
    }

    protected static void handleIndeterminate(Node node) {
        if (node instanceof TextField) {
            ((TextField) node).setText(""); //NOCHECK
            ((TextField) node).setPromptText(AbstractEditor.INDETERMINATE_STR);
        } else if (node instanceof ComboBox) {
            ((ComboBox<?>) node).getEditor().setText("");//NOCHECK
            ((ComboBox<?>) node).setPromptText(AbstractEditor.INDETERMINATE_STR);
        } else if (node instanceof ChoiceBox) {
            ((ChoiceBox<?>) node).getSelectionModel().clearSelection();
        } else if (node instanceof CheckBox) {
            ((CheckBox) node).setIndeterminate(true);
        } else if (node instanceof MenuButton) {
            ((MenuButton) node).setText(AbstractEditor.INDETERMINATE_STR);
        }
    }

    protected void setTextEditorBehavior(AbstractPropertyEditor editor, Control control, EventHandler<ActionEvent> onActionListener) {
        setTextEditorBehavior(editor, control, onActionListener, true);
    }

    protected void setTextEditorBehavior(Control control, EventHandler<ActionEvent> onActionListener) {
        setTextEditorBehavior(null, control, onActionListener, true, true);
    }

    protected void setTextEditorBehavior(AbstractPropertyEditor editor, Control control,
            EventHandler<ActionEvent> onActionListener, boolean stretchable) {
        setTextEditorBehavior(editor, control, onActionListener, stretchable, true);
    }

    protected void setTextEditorBehavior(Control control, EventHandler<ActionEvent> onActionListener, boolean addFocusListener) {
        setTextEditorBehavior(null, control, onActionListener, true, addFocusListener);
    }

    protected void setTextEditorBehavior(AbstractPropertyEditor editor, Control control,
            EventHandler<ActionEvent> onActionListener, boolean stretchable, boolean addFocusListener) {
        setCommitListener(onActionListener);
        if (stretchable) {
            EditorUtils.makeWidthStretchable(control);
        }
        if (editor != null) {
            control.disableProperty().bind(editor.disableProperty());
        }
//        setEmptyPromptText(control);
        if (control instanceof TextField) {
            ((TextField) control).setOnAction(onActionListener);
        } else if (control instanceof ComboBoxBase) {
            ((ComboBoxBase<?>) control).setOnAction(onActionListener);
        }
        if (addFocusListener && control instanceof TextInputControl) {
            addFocusListener((TextInputControl) control, onActionListener);
        }

    }

    @Override
    public EventHandler<?> getCommitListener() {
        return commitListener;
    }

    protected void setCommitListener(EventHandler<?> listener) {
        this.commitListener = listener;
    }

    protected void setNumericEditorBehavior(AbstractPropertyEditor editor, Control control,
            EventHandler<ActionEvent> onActionListener) {
        setNumericEditorBehavior(editor, control, onActionListener, true);
    }

    protected void setNumericEditorBehavior(AbstractPropertyEditor editor, Control control,
            EventHandler<ActionEvent> onActionListener, boolean stretchable) {
        setTextEditorBehavior(editor, control, onActionListener, stretchable);
        control.setOnKeyPressed(event -> {
            if (event.getCode() != KeyCode.UP && event.getCode() != KeyCode.DOWN) {
                return;
            }
            if (!(control instanceof TextField)) {
                // Apply only for text field based controls
                return;
            }
            TextField textField = (TextField) control;
            int incDecVal = 1;
            boolean shiftDown = event.isShiftDown();
            if (shiftDown) {
                incDecVal = 10;
            }
            String valStr = textField.getText();
            Double val;
            try {
                val = Double.parseDouble(valStr);
            } catch (NumberFormatException ex) {
                // may happen if the text field is empty,
                // or contains a constant string: do nothing
                return;
            }
            assert val != null;
            Double newVal = null;
            if (event.getCode() == KeyCode.UP) {
                newVal = val + incDecVal;
            } else if (event.getCode() == KeyCode.DOWN) {
                newVal = val - incDecVal;
            }
            textField.setText(EditorUtils.valAsStr(newVal));
            getCommitListener().handle(null);
            event.consume();
        });
    }

    private void addFocusListener(TextInputControl tic, EventHandler<ActionEvent> onActionListener) {
        tic.focusedProperty().addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
            if (!newValue && tic.isEditable()) {
                // focus lost
//                    System.out.println("editingProperty() set to false.");
                editingProperty().setValue(false);
            } else if (newValue && tic.isEditable()) {
                // got focus
//                    System.out.println("editingProperty() set to true.");
                editingProperty().setValue(true);
            }
        });
    }

    public void disableResetValueMenuItem() {
        resetvalueMenuItem.setDisable(true);
    }

}
