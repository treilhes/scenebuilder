package com.oracle.javafx.scenebuilder.controls.contextmenu;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.menubar.MenuAttachment;
import com.oracle.javafx.scenebuilder.api.menubar.MenuItemProvider;
import com.oracle.javafx.scenebuilder.api.menubar.PositionRequest;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
public class ContextMenuMenuProvider implements MenuItemProvider {

	private final static String REF_MENU_ID = "TBD";
	
	private final ApplicationContext context;

	public ContextMenuMenuProvider(
			@Autowired ApplicationContext context
			) {
		this.context = context;
	}

	@Override
	public List<MenuAttachment> menuItems() {
		return Arrays.asList(
				new ContextMenuMenuAttachment()
				);
	}

	public class ContextMenuMenuAttachment implements MenuAttachment {

		private Menu menu = null;

		public ContextMenuMenuAttachment() {}

		@Override
		public String getTargetId() {
			return REF_MENU_ID;
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

			menu = new Menu("xxx");
			return menu;
		}
	}
}
