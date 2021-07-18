package com.oracle.javafx.scenebuilder.inspector.actions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.Inspector;
import com.oracle.javafx.scenebuilder.api.Inspector.SectionId;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
@ActionMeta(
		nameKey = "action.name.show.edited",
		descriptionKey = "action.description.show.edited")
public class ShowSectionAction extends AbstractAction {
    
	private final Inspector inspector;
	private SectionId sectionId;
	
    public ShowSectionAction(
	        @Autowired Api api,
			@Autowired Inspector inspector) {
		super(api);
		this.inspector = inspector;
	}
    
    

    public SectionId getSectionId() {
        return sectionId;
    }



    public void setSectionId(SectionId sectionId) {
        this.sectionId = sectionId;
    }



    @Override
    public boolean canPerform() {
        return sectionId != null;
    }

    @Override
    public ActionStatus perform() {
        inspector.setExpandedSection(sectionId);
        return ActionStatus.DONE;
    }
}