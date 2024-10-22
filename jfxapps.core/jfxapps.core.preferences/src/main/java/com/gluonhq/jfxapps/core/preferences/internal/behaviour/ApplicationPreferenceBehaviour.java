/*
 * Copyright (c) 2016, 2024, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2024, Pascal Treilhes and/or its affiliates.
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
package com.gluonhq.jfxapps.core.preferences.internal.behaviour;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationEvents;
import com.gluonhq.jfxapps.core.preferences.internal.factory.BasePreference;
import com.gluonhq.jfxapps.core.preferences.internal.preference.PreferenceMetadata;
import com.gluonhq.jfxapps.core.preferences.model.PreferenceEntity;
import com.gluonhq.jfxapps.core.preferences.model.PreferenceEntity.PreferenceEntityId;
import com.gluonhq.jfxapps.core.preferences.repository.PreferenceRepository;

public class ApplicationPreferenceBehaviour extends AbstractPreferenceBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationPreferenceBehaviour.class);

    private String applicationId;

    public ApplicationPreferenceBehaviour(
            PreferenceMetadata metadata,
            PreferenceRepository repository,
            ApplicationEvents applicationEvents) {
        super(metadata, repository);
        applicationEvents.opened().subscribe(app -> {
            applicationId = app != null ? app.getClass().getName() : null;
        });
    }

    @Override
    public void write(BasePreference<?> preference) {

        try {
            var id = preference.getId();

            if (applicationId  != null) {
                var entity = new PreferenceEntity();
                entity.setId(id);
                entity.setApplication(applicationId);
                entity.setInstance("");
                entity.setJsonValue(preference.toJson());
                getRepository().saveAndFlush(entity);
            }
        } catch (JsonProcessingException e) {
            logger.error("Unable to save {}", preference, e);
        }

    }

    @Override
    public void read(BasePreference<?> preference) {

        try {
            var id = preference.getId();

            PreferenceEntity entity = null;
            if (applicationId  != null) {
                var entityId = new PreferenceEntityId();
                entityId.setId(id);
                entityId.setApplication(applicationId);
                entityId.setInstance("");

                entity = getRepository().findById(entityId).orElse(null);
            }
            if (entity != null) {
                preference.fromJson(entity.getJsonValue(), getJavaType());
            } else {
                preference.reset();
            }
        } catch (JsonProcessingException e) {
            logger.error("Unable to load {}", preference, e);
        }
    }

}
