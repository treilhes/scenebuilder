package com.gluonhq.jfxapps.registry.mapper;

import java.io.InputStream;
import java.io.OutputStream;

import com.gluonhq.jfxapps.registry.model.Registry;

public interface Mapper {

    Registry from(InputStream stream);

    void to(Registry registry, OutputStream output);

}