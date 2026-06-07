package com.oracle.javafx.scenebuilder.app.editors;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.oracle.javafx.scenebuilder.api.Documentation;
import com.oracle.javafx.scenebuilder.api.control.effect.EffectProvider;
import com.oracle.javafx.scenebuilder.metadata.custom.SbMetadata;
import com.treilhes.emc4j.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.treilhes.jfxplace.core.api.fs.FileSystem;
import com.treilhes.jfxplace.core.api.glossary.Glossary;
import com.treilhes.jfxplace.core.api.i18n.I18N;
import com.treilhes.jfxplace.core.api.ui.controller.misc.MessageLogger;
import com.treilhes.jfxplace.core.api.ui.dialog.Dialog;
import com.treilhes.jfxplace.core.metadata.AbstractMetadata;
import com.treilhes.jfxplace.core.metadata.property.ValuePropertyMetadata;
import com.treilhes.jfxplace.fxom.api.css.CssInternal;
import com.treilhes.jfxplace.fxom.api.editor.selection.FxomSelection;
import com.treilhes.jfxplace.fxom.api.subjects.FxomEvents;
import com.treilhes.jfxplace.fxom.editors.api.EditorContext;
import com.treilhes.jfxplace.fxom.model.FXOMDocument;
import com.treilhes.jfxplace.fxom.model.FXOMElement;
import com.treilhes.jfxplace.fxom.model.FXOMObject;

import javafx.scene.effect.Effect;

@ApplicationInstanceSingleton
public class EditorsContextImpl implements EditorContext {

    private final I18N i18n;
    private final Dialog dialog;
    private final FileSystem fileSystem;
    private final FxomSelection selection;
    private final FxomEvents fxomEvents;
    private final Documentation documentation;
    private final List<EffectProvider> effectProviders;
    private final SbMetadata sbMetadata;
    private final MessageLogger messageLogger;
    private final Glossary glossary;

    public EditorsContextImpl(
            I18N i18n,
            Dialog dialog,
            FileSystem fileSystem,
            FxomSelection selection,
            FxomEvents fxomEvents,
            Documentation documentation,
            SbMetadata sbMetadata,
            MessageLogger messageLogger,
            Glossary glossary,
            List<EffectProvider> effectProviders) {
        this.i18n = i18n;
        this.dialog = dialog;
        this.fileSystem = fileSystem;
        this.selection = selection;
        this.fxomEvents = fxomEvents;
        this.documentation = documentation;
        this.effectProviders = effectProviders;
        this.sbMetadata = sbMetadata;
        this.messageLogger = messageLogger;
        this.glossary = glossary;
    }


    @Override
    public I18N i18n() {
        return i18n;
    }

    @Override
    public Dialog dialog() {
        return dialog;
    }

    @Override
    public FileSystem fileSystem() {
        return fileSystem;
    }

    @Override
    public FXOMObject getCommonParentObject() {
        return selection.getAncestor();
    }

    @Override
    public void openDocumentationUrl(ValuePropertyMetadata propMeta) {
        var state = fxomEvents.selectionDidChange().get();
        documentation.openDocumentationUrl(state.getSelectedClasses(), propMeta);
    }

    @Override
    public FXOMDocument getDocument() {
        return fxomEvents.fxomDocument().get();
    }

    @Override
    public Set<Class<?>> getSelectedClasses() {
        var state = fxomEvents.selectionDidChange().get();
        return state.getSelectedClasses();
    }

    @Override
    public Set<FXOMElement> getSelectedInstances() {
        var state = fxomEvents.selectionDidChange().get();
        return state.getSelectedInstances();
    }

    @Override
    public Map<String, String> getStyleClassesMap() {
        var state = fxomEvents.selectionDidChange().get();
        var stylesheetConfig = fxomEvents.stylesheetConfig().get();
        var selectedInstances = state.getSelectedInstances();
        return CssInternal.getStyleClassesMap(stylesheetConfig, selectedInstances);
    }

    @Override
    public List<String> getThemeClasses() {
        var state = fxomEvents.selectionDidChange().get();
        var stylesheetConfig = fxomEvents.stylesheetConfig().get();
        return CssInternal.getStyleClasses(stylesheetConfig);
    }



    @Override
    public List<Class<? extends Effect>> listEffects() {
        return effectProviders.stream()
                .filter(Objects::nonNull)
                .flatMap(provider -> provider.effects().stream())
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public AbstractMetadata metadata() {
        return sbMetadata;
    }

    @Override
    public MessageLogger messageLogger() {
        return messageLogger;
    }

    @Override
    public Glossary glossary() {
        return glossary;
    }


    @Override
    public List<String> getCssProperties() {
        var selClasses = getSelectedClasses();
        return CssInternal.getCssProperties(selClasses);
    }

}