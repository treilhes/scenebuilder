/*
 * Copyright (c) 2016, 2025, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2025, Pascal Treilhes and/or its affiliates.
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
package com.gluonhq.jfxapps.boot.registry.service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gluonhq.jfxapps.boot.api.loader.extension.Extension;
import com.gluonhq.jfxapps.boot.api.registry.model.ApplicationInfo;
import com.gluonhq.jfxapps.boot.api.registry.model.LayerDefinition;
import com.gluonhq.jfxapps.boot.api.registry.model.PluginInfo;
import com.gluonhq.jfxapps.boot.registry.internal.BinaryCache;
import com.gluonhq.jfxapps.boot.registry.internal.RegistryEntityMappers;
import com.gluonhq.jfxapps.boot.registry.internal.RegistryInfoMappers;
import com.gluonhq.jfxapps.boot.registry.model.ExtensionEntity;
import com.gluonhq.jfxapps.boot.registry.model.FeatureEntity;
import com.gluonhq.jfxapps.boot.registry.model.PluginEntity;
import com.gluonhq.jfxapps.boot.registry.model.RegistryEntity;
import com.gluonhq.jfxapps.boot.registry.repository.ApplicationRepository;
import com.gluonhq.jfxapps.boot.registry.repository.ExtensionRepository;
import com.gluonhq.jfxapps.boot.registry.repository.FeatureRepository;
import com.gluonhq.jfxapps.boot.registry.repository.PluginRepository;
import com.gluonhq.jfxapps.boot.registry.repository.RegistryRepository;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;


@Service
@Transactional
public class RegistryService {

	private final static Logger logger = LoggerFactory.getLogger(RegistryService.class);

	private final BinaryCache binaryCache;
	private final RegistrySourceService registrySourceService;
	private final RegistryUpdateService registryUpdateService;
    private final RegistryRepository registryRepository;
    private final ApplicationRepository applicationRepository;
    private final FeatureRepository featureRepository;
    private final PluginRepository pluginRepository;
    private final ExtensionRepository extensionRepository;
    private final RegistryInfoMappers infoMappers;
    private final RegistryEntityMappers entityMappers;

    public RegistryService(
    		BinaryCache binaryCache,
    		RegistrySourceService registrySourceService,
    		RegistryUpdateService registryUpdateService,
            RegistryRepository registryRepository,
            ApplicationRepository applicationRepository,
            FeatureRepository featureRepository,
            PluginRepository pluginRepository,
            ExtensionRepository extensionRepository,
            RegistryInfoMappers infoMappers,
            RegistryEntityMappers entityMappers) {
    	this.binaryCache = binaryCache;
    	this.registrySourceService = registrySourceService;
    	this.registryUpdateService = registryUpdateService;
        this.registryRepository = registryRepository;
        this.applicationRepository = applicationRepository;
        this.featureRepository = featureRepository;
        this.pluginRepository = pluginRepository;
        this.extensionRepository = extensionRepository;

        this.infoMappers = infoMappers;
        this.entityMappers = entityMappers;
    }

    @PostConstruct
	protected void init() {
    	if (!isInitialized()) {

    		logger.info("Registry is not initialized, searching for updates");

			registrySourceService.findAll().forEach(registrySource -> {
				if (registrySource.isMandatory()) {
					var registry = registryUpdateService.loadLatest(registrySource);
					registry.getApplications().forEach(a -> a.setInstalled(true));
					save(registry);
				}
			});
    	}
	}

	public boolean isInitialized() {
		return registryRepository.count() > 0;
	}

	public ApplicationInfo applicationInfo(UUID applicationId) {
		return applicationRepository.findById(applicationId)
				.map(a -> {
					var info = infoMappers.map(a);
					info.setImage(binaryCache.get(a.getId(), "image"));
					info.setI18n(binaryCache.get(a.getId(), "i18n"));
					info.setSplash(binaryCache.get(a.getId(), "splash"));
					return info;
				})
				.orElse(null);
	};

    public PluginInfo pluginInfo(UUID pluginId) {
        return pluginRepository.findById(pluginId).map(infoMappers::map).orElse(null);
    }

    public Set<ApplicationInfo> listApplicationsInfo() {
        return applicationRepository.findAll().stream().map(infoMappers::map).collect(Collectors.toSet());
    }

    public Set<PluginInfo> listApplicationPluginsInfo(UUID applicationId) {
        return pluginRepository.findByTarget(applicationId).stream().map(infoMappers::map).collect(Collectors.toSet());
    }

	public void save(@Valid RegistryEntity registry) {
		registryRepository.save(registry);
	}

	public LayerDefinition computeLayerDefinition(UUID applicationId) {
		var application = applicationRepository.findByInstalledTrueAndId(applicationId);
		var plugins = pluginRepository.findByInstalledTrueAndTarget(applicationId);
		return application.map(entityMappers::map).map(l -> this.populate(l, plugins)).orElse(null);
	}



	private LayerDefinition populate(LayerDefinition layer, Set<PluginEntity> plugins) {
		var uuidToExtensions = flatten(plugins);
        return recurse(layer, uuidToExtensions);
	}

	private Map<UUID, Set<ExtensionEntity>> flatten(Set<PluginEntity> plugins) {
		// Group by target UUID and accumulate extensions into a list
		var uuidToExtensions = plugins.stream().flatMap(p -> p.getFeatures().stream())
				.collect(Collectors.groupingBy(FeatureEntity::getTarget, // Group by the target UUID
						Collectors.flatMapping( // Flatten the extensions into a single list
								f -> f.getExtensions().stream(), Collectors.toSet())));

		return uuidToExtensions;
	}

	private LayerDefinition recurse(LayerDefinition layerDef, Map<UUID, Set<ExtensionEntity>> map) {
		var extensions = map.get(layerDef.getId());
		if (extensions != null) {
			extensions.forEach(e -> layerDef.getChildren().add(entityMappers.map(e)));
		}
		layerDef.getChildren().forEach(c -> recurse(c, map));
        return layerDef;
    }

}
