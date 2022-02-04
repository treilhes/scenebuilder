/*
 * Copyright (c) 2016, 2022, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2022, Pascal Treilhes and/or its affiliates.
 * Copyright (c) 2012, 2014, Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation and Gluon nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.oracle.javafx.scenebuilder.api.action;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oracle.javafx.scenebuilder.api.i18n.I18N;

import javafx.scene.input.KeyCombination;

public abstract class AbstractAction implements Action {

    private static final Logger logger = LoggerFactory.getLogger(AbstractAction.class);

	private final String nameI18nKey;
	private final String descriptionI18nKey;
	private final String rawAccelerator;
	private final List<ActionExtension<?>> extensions = new ArrayList<>();


	public AbstractAction(ActionExtensionFactory extensionFactory) {
		ActionMeta actionMeta = this.getClass().getAnnotation(ActionMeta.class);

		if (actionMeta == null) {
			throw new RuntimeException("Class inheriting AbstractAction class must be annotated with @ActionMeta");
		}

		nameI18nKey = actionMeta.nameKey();
		descriptionI18nKey = actionMeta.descriptionKey();
		rawAccelerator = actionMeta.accelerator().isBlank() ? null : actionMeta.accelerator();

		extensions.addAll(extensionFactory.getExtensions(this));
	}

	@Override
	public String getUniqueId() {
		return this.getClass().getName();
	}

	@Override
	public String getName() {
		return nameI18nKey == null ? null : I18N.getString(nameI18nKey);
	}

	@Override
	public String getDescription() {
		return descriptionI18nKey == null ? null : I18N.getString(descriptionI18nKey);
	}

	@Override
	public KeyCombination getWishedAccelerator() {
		if (rawAccelerator == null) {
			return null;
		}
		return KeyCombination.valueOf(rawAccelerator);
	}

	@Override
	public ActionStatus checkAndPerform() {
		try {
            if (canPerform()) {
            	return perform();
            }
        } catch (Exception e) {
            logger.error("Unable to complete action {}", this.getClass().getName(), e);
            return ActionStatus.FAILED;
        }
        return ActionStatus.CANCELLED;
	}

    @Override
    public final ActionStatus perform() {
        if (!extensions.isEmpty()) {
            extensions.stream().filter(ext -> ext.canPerform()).forEach(ext -> {
                logger.debug("Will Execute prePerform on {}", ext.getClass());
                ext.prePerform();
                logger.info("Executed prePerform on {}", ext.getClass());
            });
        }

        logger.debug("Will execute perform on {}", getClass());
        ActionStatus status = doPerform();
        logger.info("Executed perform on {} : {}", getClass(), status);

        if (!extensions.isEmpty()) {
            extensions.stream().filter(ext -> ext.canPerform()).forEach(ext -> {
                logger.info("Will execute postPerform on {}", ext.getClass());
                ext.postPerform();
                logger.info("Executed postPerform on {}", ext.getClass());
            });
        }

        return status;
    }

    @Override
    public abstract boolean canPerform();

    public abstract ActionStatus doPerform();
}
