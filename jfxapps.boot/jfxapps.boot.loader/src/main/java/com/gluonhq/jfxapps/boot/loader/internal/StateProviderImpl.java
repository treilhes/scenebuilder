package com.gluonhq.jfxapps.boot.loader.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.loader.StateProvider;
import com.gluonhq.jfxapps.boot.loader.extension.Extension;
import com.gluonhq.jfxapps.boot.loader.internal.repository.ApplicationRepository;
import com.gluonhq.jfxapps.boot.loader.model.JfxApps;
import com.gluonhq.jfxapps.boot.maven.client.api.RepositoryClient;
import com.gluonhq.jfxapps.boot.registry.RegistryManager;
import com.gluonhq.jfxapps.registry.model.Registry;

@Component
public class StateProviderImpl implements StateProvider {

    /** The Constant logger. */
    private final static Logger logger = LoggerFactory.getLogger(StateProviderImpl.class);

    private final RegistryManager registryManager;
    private final RepositoryClient repositoryClient;
    private final ApplicationRepository repository;
    private final LoaderMappers mappers;

    protected StateProviderImpl(RegistryManager registryManager, LoaderMappers mappers, RepositoryClient repositoryClient,
            ApplicationRepository repository) {
        super();
        this.registryManager = registryManager;
        this.mappers = mappers;
        this.repositoryClient = repositoryClient;
        this.repository = repository;
    }

    @Override
    public JfxApps bootState() {
        return hasSavedState() ? buildSavedState() : buildInitialState();
    }

    public boolean hasSavedState() {
        return repository.existsById(Extension.ROOT_ID);
    }

    private JfxApps buildSavedState() {
        var root = repository.findById(Extension.ROOT_ID).orElseThrow();
        var applications = repository.findByIdNot(Extension.ROOT_ID);

        JfxApps target = mappers.mapToJfxApps(root, repositoryClient);

        applications.forEach(app -> target.addApplication(mappers.mapToApplication(app, repositoryClient)));

        return target;
    }

    /**
     *
     * @return the expected state
     */
    private JfxApps buildInitialState() {
        Registry bootRegistry = registryManager.bootRegistry();

        if (bootRegistry.getApplications().size() != 1) {
            throw new RuntimeException("Boot registry contains more or less than 1 application");
        }

        if (bootRegistry.getExtensions().size() > 0) {
            logger.warn("Boot registry extensions are ignored by default and won't be loaded");
        }

        var bootApp = bootRegistry.getApplications().iterator().next();

        if (!Extension.ROOT_ID.equals(bootApp.getUuid())) {
            logger.error("Invalid boot application uuid, expected {} but was {}", Extension.ROOT_ID, bootApp.getUuid());
            throw new RuntimeException("Invalid boot application uuid");
        }

        JfxApps target = mappers.mapToJfxApps(bootApp, repositoryClient);

        var registry = registryManager.installedRegistry();

        registry.getApplications()
                .forEach(app -> target.addApplication(mappers.mapToApplication(app, repositoryClient)));

        return target;
    }

}
