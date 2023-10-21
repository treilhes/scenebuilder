package com.oracle.javafx.scenebuilder.core.loader.extension;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtensionValidator {

    private final static Logger logger = LoggerFactory.getLogger(ExtensionValidator.class);

    private ExtensionValidator() {
    }

    public static boolean isValid(Extension extension) {
        boolean isValid = true;

        if (Objects.isNull(extension.getId())) {
            logger.error("Extension method getId() can't return null!");
            isValid = false;
        }

        if (Objects.isNull(extension.getParentId()) && !extension.getId().equals(Extension.ROOT_ID)) {
            logger.error("Extension method getParentId() can't return null!");
            isValid = false;
        }

        if (Objects.isNull(extension.localContextClasses())) {
            logger.error("Extension method localContextClasses() can't return null!");
            isValid = false;
        }

        if (extension instanceof OpenExtension open) {
            if (Objects.isNull(open.exportedContextClasses())) {
                logger.error("Extension method exportedContextClasses() can't return null!");
                isValid = false;
            }

            if (Objects.nonNull(open.exportedContextClasses()) && Objects.nonNull(open.localContextClasses())) {
                List<Class<?>> common = new ArrayList<>(open.exportedContextClasses());
                common.retainAll(open.localContextClasses());

                if (!common.isEmpty()) {
                    logger.error("Duplicate classes found, same class can't be both local and exported, culprit classes:");
                    common.forEach(c -> logger.error(c.getName()));
                    isValid = false;
                }
            }
        }


        return isValid;
    }
}
