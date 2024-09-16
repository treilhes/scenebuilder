/*
 * Copyright (c) 2016, 2023, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2023, Pascal Treilhes and/or its affiliates.
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
package com.gluonhq.jfxapps.core.maven.preferences.global;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.api.maven.Repository;
import com.gluonhq.jfxapps.core.api.preferences.ListPreferences;
import com.gluonhq.jfxapps.core.api.preferences.PreferencesContext;

@Component
public class MavenRepositoriesPreferences extends ListPreferences<MavenRepositoryPreferences, Repository> {

	// NODE
    protected static final String NODE_NAME = "REPOSITORIES"; //NOCHECK

    public MavenRepositoriesPreferences(@Autowired PreferencesContext preferencesContext) {
    	super(preferencesContext, NODE_NAME, MavenRepositoryPreferences.keyProvider(), MavenRepositoryPreferences.defaultProvider());
    }

    public MavenRepositoryPreferences getRecordRepository(Repository repository) {
    	return getRecord(repository);
    }

    public MavenRepositoryPreferences getRecordRepository(String id) {
        return getRecord(id);
    }

    public void addRecordRepository(String key, MavenRepositoryPreferences object) {
        addRecordRepository(key, object);
    }

    public void removeRecordRepository(String id) {
        removeRecord(id);
    }

    public List<Repository> getRepositories() {
        return getRecords().values()
                .stream()
                .map(p -> p.getValue())
                .collect(Collectors.toList());
    }

}
