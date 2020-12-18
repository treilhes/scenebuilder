package com.oracle.javafx.scenebuilder.ext.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.action.AbstractActionExtension;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.ext.theme.document.I18NResourcePreference;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class ApplyI18nContentResourceExtension extends AbstractActionExtension<ApplyI18nContentAction> {

	private final I18NResourcePreference i18NResourcePreference;

	public ApplyI18nContentResourceExtension(
			@Autowired @Lazy I18NResourcePreference i18NResourcePreference
			) {
		super();
		this.i18NResourcePreference = i18NResourcePreference;
	}

	@Override
	public boolean canPerform() {
		return i18NResourcePreference.getValue() != null;
	}

	@Override
	public void prePerform() {
		List<ResourceBundle> bundles = i18NResourcePreference.getValue().stream()
			.map(f -> new File(URI.create(f)))
			.map(f -> readPropertyResourceBundle(f))
			.collect(Collectors.toList());
		getExtendedAction().getActionConfig().getBundles().addAll(bundles);
	}

	private static PropertyResourceBundle readPropertyResourceBundle(File f) {
        PropertyResourceBundle result;
        try( Reader reader = new InputStreamReader(new FileInputStream(f), Charset.forName("UTF-8")) ) {
            result = new PropertyResourceBundle(reader); //NOI18N
        } catch (IOException ex) {
            result = null;
        }
        return result;
    }

}
