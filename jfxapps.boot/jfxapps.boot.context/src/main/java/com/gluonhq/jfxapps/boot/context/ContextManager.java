package com.gluonhq.jfxapps.boot.context;

import java.util.List;
import java.util.UUID;

public interface ContextManager {

    SbContext get(UUID contextId);

    boolean exists(UUID contextId);

    SbContext create(UUID parentContextId, UUID contextId, Class<?>[] classes, List<Object> singletonInstances,
            MultipleProgressListener progressListener);

    void clear();

    void close(UUID id);

}