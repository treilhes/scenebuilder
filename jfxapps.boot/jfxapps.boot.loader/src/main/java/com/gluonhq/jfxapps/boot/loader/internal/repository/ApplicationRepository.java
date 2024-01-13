package com.gluonhq.jfxapps.boot.loader.internal.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gluonhq.jfxapps.boot.loader.internal.model.Application;

public interface ApplicationRepository extends JpaRepository<Application, UUID>{
    List<Application> findByRegistry(UUID registryId);
    List<Application> findByIdNot(UUID id);
}
