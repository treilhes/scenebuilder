package com.oracle.javafx.scenebuilder.api.editor.job;

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
public class ExtendedJob<T extends Job> extends Job {

	private List<JobExtension<T>> extensions;

	private boolean extended = false;
	private final Job job;

	@SuppressWarnings("unchecked")
	public ExtendedJob(T job) {
		super(job.getContext(), job.getEditorController());
		this.job = job;

		ResolvableType resolvable = ResolvableType.forClassWithGenerics(JobExtension.class, job.getClass());
		String[] beanNamesForType = getContext().getBeanNamesForType(resolvable);

		if (beanNamesForType.length > 0) {
			extensions = Arrays.asList(beanNamesForType).stream()
					.map(b -> (JobExtension<T>)getContext().getBean(b)).collect(Collectors.toList());
		}

		if (extensions != null) {
			extensions.forEach(ext -> ext.setExtendedJob(job));
			extended = true;
		}

	}

	@Override
	public boolean isExecutable() {
		return job.isExecutable();
	}

	@Override
	public void execute() {
		if (extended) {
			extensions.stream().filter(ext -> ext.isExecutable()).forEach(ext -> ext.preExecute());
		}

		job.execute();

		if (extended) {
			extensions.stream().filter(ext -> ext.isExecutable()).forEach(ext -> ext.postExecute());
		}
	}

	@Override
	public void undo() {
		if (extended) {
			extensions.forEach(ext -> ext.preUndo());
		}

		job.undo();

		if (extended) {
			extensions.forEach(ext -> ext.postUndo());
		}
	}

	@Override
	public void redo() {
		if (extended) {
			extensions.forEach(ext -> ext.preRedo());
		}

		job.redo();

		if (extended) {
			extensions.forEach(ext -> ext.postRedo());
		}
	}

	@Override
	public String getDescription() {
		return job.getDescription();
	}

	public Job getExtendedJob() {
		return job;
	}



}
