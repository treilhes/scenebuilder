package com.oracle.javafx.scenebuilder.gluon.editor.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.editor.job.AbstractJobExtension;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.ext.theme.document.ThemePreference;
import com.oracle.javafx.scenebuilder.gluon.alert.WarnThemeAlert;
import com.oracle.javafx.scenebuilder.kit.editor.job.atomic.SetFxomRootJob;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class SetFxomRootJobExtension extends AbstractJobExtension<SetFxomRootJob> {

	@Autowired private Editor editorController;
	@Autowired private ThemePreference themePreference;

	@Override
	public void postExecute() {
		WarnThemeAlert.showAlertIfRequired(themePreference, getExtendedJob().getNewRoot(), editorController.getOwnerWindow());
	}

}
