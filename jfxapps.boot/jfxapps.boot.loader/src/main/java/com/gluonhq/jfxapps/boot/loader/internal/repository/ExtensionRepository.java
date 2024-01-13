package com.gluonhq.jfxapps.boot.loader.internal.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gluonhq.jfxapps.boot.loader.internal.model.Extension;

public interface ExtensionRepository extends JpaRepository<Extension, UUID>{
    List<Extension> findByRegistry(UUID registryId);
}
