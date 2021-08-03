package com.oracle.javafx.scenebuilder.editors.menu;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.control.effect.EffectProvider;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.menubar.MenuItemAttachment;
import com.oracle.javafx.scenebuilder.api.menubar.MenuItemProvider;
import com.oracle.javafx.scenebuilder.api.menubar.PositionRequest;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.editors.actions.SetEffectAction;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.Effect;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
public class SetEffectsMenuProvider implements MenuItemProvider {

    private final static String MODIFY_MENU_ID = "modifyMenu";
    private final static String SET_EFFECTS_MENU_ID = "setEffect";

    private final ApplicationContext context;
    private final List<Class<? extends Effect>> effects;

    public SetEffectsMenuProvider(
            @Autowired ApplicationContext context,
            @Autowired List<EffectProvider> effectProviders) {
        this.context = context;
        this.effects = effectProviders.stream().flatMap(p -> p.effects().stream()).collect(Collectors.toList());
    }

    @Override
    public List<MenuItemAttachment> menuItems() {
        return Arrays.asList(MenuItemAttachment.separator(MODIFY_MENU_ID, PositionRequest.AsLastChild),
                new SetEffectsMenuItemAttachment());
    }

    public class SetEffectsMenuItemAttachment implements MenuItemAttachment {

        private Menu menu = null;

        public SetEffectsMenuItemAttachment() {
            super();
        }

        @Override
        public String getTargetId() {
            return MODIFY_MENU_ID;
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

            menu = new Menu(I18N.getString("menu.title.add.effect"));
            menu.setId(SET_EFFECTS_MENU_ID);
            
            for (Class<? extends Effect> c : effects) {
                MenuItem mi = new MenuItem(c.getSimpleName());
                mi.setUserData(c);
                SetEffectAction action = context.getBean(SetEffectAction.class);
                action.setEffectClass(c);
                mi.setOnAction(e -> action.extend().perform());
                menu.getItems().add(mi);
            }

            menu.setOnMenuValidation(e -> {
                menu.getItems().forEach(i -> {
                    Class<? extends Effect> c = (Class<? extends Effect>)i.getUserData();
                    SetEffectAction action = context.getBean(SetEffectAction.class);
                    action.setEffectClass(c);
                    i.setDisable(!action.extend().canPerform());
                });
            });
            return menu;
        }
        
    }

}
