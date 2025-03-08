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
package com.gluonhq.jfxapps.boot.loader.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.api.loader.LoadType;
import com.gluonhq.jfxapps.boot.api.maven.RepositoryClient;
import com.gluonhq.jfxapps.boot.api.registry.RegistryManager;
import com.gluonhq.jfxapps.boot.api.registry.model.LayerDefinition;
import com.gluonhq.jfxapps.boot.loader.StateProvider;
import com.gluonhq.jfxapps.boot.loader.content.CreateOnlyContentProvider;
import com.gluonhq.jfxapps.boot.loader.content.ExtensionContentProvider;
import com.gluonhq.jfxapps.boot.loader.content.MavenExtensionProvider;
import com.gluonhq.jfxapps.boot.loader.internal.jpa.model.Extension;
import com.gluonhq.jfxapps.boot.loader.internal.repository.ExtensionRepository;
import com.gluonhq.jfxapps.boot.loader.model.LoadState;
import com.gluonhq.jfxapps.boot.loader.model.LoadableContent;

@Component
public class StateProviderImpl implements StateProvider {

    /** The Constant logger. */
    private final static Logger logger = LoggerFactory.getLogger(StateProviderImpl.class);

    private final static UUID ROOT_ID = com.gluonhq.jfxapps.boot.api.loader.extension.Extension.ROOT_ID;

    private final RegistryManager registryManager;
    private final RepositoryClient repositoryClient;
    private final ExtensionRepository repository;
    private final LoaderMappers mappers;

    // @formatter:off
    protected StateProviderImpl(
    		RegistryManager registryManager,
    		LoaderMappers mappers,
    		RepositoryClient repositoryClient,
            ExtensionRepository repository) {
    	// @formatter:on
        super();
        this.registryManager = registryManager;
        this.mappers = mappers;
        this.repositoryClient = repositoryClient;
        this.repository = repository;
    }

    @Override
    public LoadableContent applicationState(UUID applicationId, LoadType loadType) {

        var savedState = loadSavedState(applicationId);

        var registryState = (savedState.isEmpty() || loadType != LoadType.LastSuccessfull)
                ? loadRegistryState(applicationId)
                : Optional.<LayerDefinition>empty();

		if (savedState.isEmpty()) {
			// first time running the application, change loadType to install the application
			loadType = LoadType.FullUpdate;
		}
        return mergeStates(savedState, registryState, loadType);
    }

    private LoadableContent mergeStates(Optional<Extension> savedState, Optional<LayerDefinition> registryState, LoadType loadType) {
        return new Merger().mergeStates(savedState, registryState, loadType);
    }

    private Optional<Extension> loadSavedState(UUID applicationId) {
        return repository.findById(applicationId);
    }

    /**
     *
     * @return the expected state
     */
    private Optional<LayerDefinition> loadRegistryState(UUID applicationId) {
        return Optional.ofNullable(registryManager.computeLayerDefinition(applicationId));
    }

    @Override
    public void saveState(Extension extension) {
        repository.save(extension);
    }

    private class Merger {

        private final Map<UUID, Extension> flattenedSaved = new HashMap<>();
        private final Map<UUID, LayerDefinition> flattenedRegistry = new HashMap<>();

        private LoadableContent mergeStates(Optional<Extension> savedState, Optional<LayerDefinition> registryState, LoadType loadType) {

            savedState.ifPresent(e -> flattenExtension(Set.of(e)));
            registryState.ifPresent(e -> flattenLayerDefinition(Set.of(e)));

            LoadableContent loadableContent = mergeItem(null, savedState.orElse(null), registryState.orElse(null), loadType);


            return loadableContent;
        }

        private LoadableContent mergeItem(LoadableContent parent, Extension savedState, LayerDefinition registryState,
                LoadType loadType) {

            if (savedState == null && registryState == null) {
                throw new IllegalArgumentException("Both saved and registry state are null");
            }

            UUID savedId = savedState != null ? savedState.getId() : null;
            UUID registryId = registryState != null ? registryState.getId() : null;

            if (savedState != null && registryState != null && !Objects.equals(savedId, registryId)) {
                throw new IllegalArgumentException("Both saved and registry state must share the same id");
            }

            LoadableContent loadableContent = null;
            boolean updated = false;

			switch (loadType) {
			case LastSuccessfull: {
				if (savedState != null) {
					loadableContent = new LoadableContent();
	            	loadableContent.setExtension(savedState);
	                loadableContent.setLoadState(mappers.map(savedState.getState()));
	            }
				break;
			}
			case LocalUpdateOnly:
			case UpdateOnly: {
				if (savedState != null) {
					loadableContent = new LoadableContent();
	            	loadableContent.setExtension(savedState);
	                loadableContent.setLoadState(mappers.map(savedState.getState()));

	                if (registryState != null) {
						if (registryState.getGroupId().equals(savedState.getGroupId())
								&& registryState.getArtifactId().equals(savedState.getArtifactId())
								&& registryState.getVersion().equals(savedState.getVersion())) {
							// No change
						} else {
							// Update
							savedState.setGroupId(registryState.getGroupId());
			                savedState.setArtifactId(registryState.getArtifactId());
			                savedState.setVersion(registryState.getVersion());
			                updated = true;
						}

					} else {
						loadableContent.setLoadState(LoadState.Deleted);
					}
	            }
				break;
			}
			case LocalFullUpdate:
			case FullUpdate: {
				if (registryState != null) {
	            	Extension extension = new Extension();
	                extension.setId(registryState.getId());
	                extension.setGroupId(registryState.getGroupId());
	                extension.setArtifactId(registryState.getArtifactId());
	                extension.setVersion(registryState.getVersion());

	                loadableContent = new LoadableContent();
	                loadableContent.setExtension(extension);
	                loadableContent.setLoadState(LoadState.Unloaded);
				} else if (savedState != null) {
					loadableContent = new LoadableContent();
					loadableContent.setExtension(savedState);
					loadableContent.setLoadState(LoadState.Deleted);
				}
				break;
			}

			default:
				throw new RuntimeException("Unknown load type " + loadType);
			}

			if (loadableContent != null) {

				if (parent != null) {
					parent.addExtension(loadableContent);
				}

				var groupId = loadableContent.getExtension().getGroupId();
				var artifactId = loadableContent.getExtension().getArtifactId();
				var version = loadableContent.getExtension().getVersion();

				var mavenProvider = new MavenExtensionProvider(groupId, artifactId, version);
				ExtensionContentProvider contentProvider = mavenProvider;

				switch (loadType) {
				 	case LastSuccessfull:
				 		if (!updated) {
							contentProvider = new CreateOnlyContentProvider(mavenProvider);
						}
					case LocalUpdateOnly:
					case LocalFullUpdate:
						mavenProvider.setRepositoryClient(repositoryClient.localOnly());
					case UpdateOnly:
					case FullUpdate:
						mavenProvider.setRepositoryClient(repositoryClient);
						break;
					default:
						throw new RuntimeException("Unknown load type " + loadType);
				}

				loadableContent.setContentProvider(contentProvider);
			}

			if (savedState != null) {
				for (Extension ext : savedState.getExtensions()) {
					var registryExt = flattenedRegistry.remove(ext.getId());
					mergeItem(loadableContent, ext, registryExt, loadType);
				}
			}

			if (registryState != null) {
				for (LayerDefinition ext : registryState.getChildren()) {
					var registryExt = flattenedRegistry.remove(ext.getId());
					mergeItem(loadableContent, flattenedSaved.get(ext.getId()), registryExt, loadType);
				}
			}

            return loadableContent;
        }

        /**
         * Flatten.
         *
         * @param flattened    the flattened
         * @param extensionSet the extension set
         */
        private void flattenLayerDefinition(Set<? extends LayerDefinition> extensionSet) {
            extensionSet.forEach(ext -> {
                flattenedRegistry.put(ext.getId(), ext);
                flattenLayerDefinition(ext.getChildren());
            });
        }

        private void flattenExtension(Set<? extends Extension> extensionSet) {
            extensionSet.forEach(ext -> {
                flattenedSaved.put(ext.getId(), ext);
                flattenExtension(ext.getExtensions());
            });
        }
    }
}
