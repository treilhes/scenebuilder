package org.scenebuilder.ext.script;

import java.io.IOException;

import org.scenebuilder.ext.script.preference.global.StaticLoadPreference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.lifecycle.InitWithDocument;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.ext.LoaderCapabilitiesManager;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
public class LoaderCapabilitiesManagerImpl implements LoaderCapabilitiesManager, InitWithDocument {

    private static Logger logger = LoggerFactory.getLogger(LoaderCapabilitiesManagerImpl.class);

    private static boolean staticLoad = true;

    public LoaderCapabilitiesManagerImpl() {

    }

    @Autowired
    public LoaderCapabilitiesManagerImpl(
            @Autowired StaticLoadPreference staticLoadPreference,
            @Autowired DocumentManager docManager) {

        staticLoadPreference.getObservableValue().addListener((ob, o, n) -> {
            setStaticLoadingEnabled(n);
            FXOMDocument fxomDocument = docManager.fxomDocument().get();

            if (fxomDocument != null) {
                try {
                    FXOMDocument clone = new FXOMDocument(fxomDocument.getFxmlText(false),
                                fxomDocument.getLocation(),
                                fxomDocument.getClassLoader(),
                                fxomDocument.getResources());
                    docManager.fxomDocument().set(clone);
                } catch (IOException e) {
                    logger.error("Unable to update document after changing loader capabilities" , e);
                }
            }

        });
    }

    @Override
    public boolean isStaticLoadingEnabled() {
        return staticLoad;
    }

    @Override
    public void setStaticLoadingEnabled(boolean staticLoad) {
        LoaderCapabilitiesManagerImpl.staticLoad = staticLoad;
    }

    @Override
    public void initWithDocument() {
        // TODO Auto-generated method stub
        
    }

}