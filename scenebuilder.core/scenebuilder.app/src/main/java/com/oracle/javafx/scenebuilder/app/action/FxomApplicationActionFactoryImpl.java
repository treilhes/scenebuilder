package com.oracle.javafx.scenebuilder.app.action;

import com.gluonhq.jfxapps.core.api.action.Action;
import com.gluonhq.jfxapps.core.api.action.ActionFactory;
import com.gluonhq.jfxapps.core.api.fxom.action.FxomApplicationActionFactory;
import com.treilhes.emc4j.boot.api.context.annotation.ApplicationInstanceSingleton;

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
