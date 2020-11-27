package com.oracle.javafx.scenebuilder.api;

import javafx.beans.property.Property;
import javafx.scene.input.TransferMode;

public interface Drag {

	DragSource getDragSource();

	void begin(DragSource dragSource);

	void setDropTarget(DropTarget rootDropTarget);

	TransferMode[] getAcceptedTransferModes();

	void commit();

	void end();

	boolean isDropAccepted();

	DropTarget getDropTarget();

	Property<DragSource> dragSourceProperty();

	Property<DropTarget> dropTargetProperty();

}
