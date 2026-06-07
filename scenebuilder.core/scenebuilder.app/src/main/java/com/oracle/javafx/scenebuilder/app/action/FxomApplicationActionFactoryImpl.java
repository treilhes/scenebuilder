package com.oracle.javafx.scenebuilder.app.action;

import com.treilhes.emc4j.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.treilhes.jfxplace.core.api.action.Action;
import com.treilhes.jfxplace.core.api.instance.ActionFactory;
import com.treilhes.jfxplace.fxom.api.action.FxomApplicationActionFactory;

@ApplicationInstanceSingleton
public class FxomApplicationActionFactoryImpl implements FxomApplicationActionFactory {

    private final ActionFactory actionFactory;

    public FxomApplicationActionFactoryImpl(ActionFactory actionFactory) {
        this.actionFactory = actionFactory;
    }

    @Override
    public Action closeInstance(boolean force) {
        return actionFactory.create(FxomCloseInstanceAction.class, a -> a.setForce(force));
    }
}
