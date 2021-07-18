package com.oracle.javafx.scenebuilder.ui.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.dock.View;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.dock.DockViewController;
import com.oracle.javafx.scenebuilder.core.dock.DockViewController.ViewItem;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
@ActionMeta(nameKey = "action.name.toggle.view", descriptionKey = "action.description.toggle.view")
public class ToggleViewVisibilityAction extends AbstractAction {

    private final DockViewController dockViewController;
    
    private ViewItem viewItem;
    
    public ToggleViewVisibilityAction(
            @Autowired Api api,
            @Autowired DockViewController dockViewController) {
        super(api);
        this.dockViewController = dockViewController;
    }

    public ViewItem getViewItem() {
        return viewItem;
    }

    public void setViewItem(ViewItem viewItem) {
        this.viewItem = viewItem;
    }

    @Override
    public boolean canPerform() {
        return true;
    }

    @Override
    public ActionStatus perform() {
        ApplicationContext ctx = getApi().getContext();
        View view = ctx.getBean(viewItem.getViewClass());
        if (view.isVisible()) {
            dockViewController.performCloseView(view);
        } else  {
            dockViewController.performOpenView(viewItem);
        }
        return ActionStatus.DONE;
    }
}