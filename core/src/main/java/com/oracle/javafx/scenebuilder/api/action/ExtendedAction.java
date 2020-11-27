package com.oracle.javafx.scenebuilder.api.action;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
@ActionMeta
public class ExtendedAction<T extends AbstractAction> extends AbstractAction {

	private List<ActionExtension<T>> extensions;

	private boolean extended = false;
	private final AbstractAction action;

	@SuppressWarnings("unchecked")
	public ExtendedAction(T action) {
		super(action.getContext());

		this.action = action;

		ResolvableType resolvable = ResolvableType.forClassWithGenerics(ActionExtension.class, action.getClass());
		String[] beanNamesForType = getContext().getBeanNamesForType(resolvable);

		if (beanNamesForType.length > 0) {
			extensions = Arrays.asList(beanNamesForType).stream()
					.map(b -> (ActionExtension<T>)getContext().getBean(b)).collect(Collectors.toList());
		}

		if (extensions != null) {
			extensions.forEach(ext -> ext.setExtendedAction(action));
			extended = true;
		}

	}

	@Override
	public boolean canPerform() {
		return action.canPerform();
	}

	@Override
	public void perform() {
		if (extended) {
			extensions.stream().filter(ext -> ext.canPerform()).forEach(ext -> ext.prePerform());
		}

		action.perform();

		if (extended) {
			extensions.stream().filter(ext -> ext.canPerform()).forEach(ext -> ext.postPerform());
		}
	}

	public Action getExtendedAction() {
		return action;
	}



}
