package com.gluonhq.jfxapps.registry.mapper.impl;

import java.io.InputStream;
import java.io.OutputStream;

import com.gluonhq.jfxapps.registry.mapper.InvalidRegistryException;
import com.gluonhq.jfxapps.registry.mapper.Mapper;
import com.gluonhq.jfxapps.registry.model.Registry;

public class XmlMapper implements Mapper {

    private static final com.fasterxml.jackson.dataformat.xml.XmlMapper mapper = new com.fasterxml.jackson.dataformat.xml.XmlMapper();

    @Override
    public Registry from(InputStream stream) {
        try {
            return mapper.readValue(stream, Registry.class);
        } catch (Exception e) {
            throw new InvalidRegistryException(e);
        }
    }

    @Override
    public void to(Registry registry, OutputStream output) {
        try {
            mapper.writeValue(output, registry);
        } catch (Exception e) {
            throw new InvalidRegistryException(e);
        }
    }
}
