package com.oracle.javafx.scenebuilder.core.loader.model;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class ModelStore {

    private ObjectMapper mapper;

    public ModelStore() {
        super();
        this.mapper = new ObjectMapper();
        this.mapper.configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true);
    }

    public Application read(Path file) throws IOException {
        Application application = mapper.readValue(file.toFile(), Application.class);
        return application;
    }

    public Application read(InputStream file) throws IOException {
        Application application = mapper.readValue(file, Application.class);
        return application;
    }

    public void write(Path file, Application application) throws IOException {
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        writer.writeValue(file.toFile(), application);
    }
}
