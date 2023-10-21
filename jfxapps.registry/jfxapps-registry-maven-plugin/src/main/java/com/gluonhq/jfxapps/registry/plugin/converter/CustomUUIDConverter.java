package com.gluonhq.jfxapps.registry.plugin.converter;

import java.util.UUID;

import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.converters.basic.AbstractBasicConverter;


public class CustomUUIDConverter extends AbstractBasicConverter {
    private static final Class<?> TYPE = UUID.class;

    @Override
    public boolean canConvert(Class<?> type) { return type.equals(TYPE); }

    @Override
    public Object fromString(String string) throws ComponentConfigurationException {
        Object object = null;

        try {
            object = UUID.fromString(string);
        } catch (Exception exception) {
            String message =
                "Unable to convert '" + string + "' to " + TYPE.getName();

            throw new ComponentConfigurationException(message, exception);
        }

        return object;
    }
}
