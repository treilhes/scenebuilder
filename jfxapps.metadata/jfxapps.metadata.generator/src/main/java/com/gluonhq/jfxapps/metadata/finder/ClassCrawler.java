package com.gluonhq.jfxapps.metadata.finder;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.metadata.bean.BeanMetaData;
import com.gluonhq.jfxapps.metadata.util.Report;

public class ClassCrawler {

    private final static Logger logger = LoggerFactory.getLogger(ClassCrawler.class);

    private Map<Class<?>, BeanMetaData<?>> classes = new HashMap<>();

    public void crawl(Set<Path> jars, SearchContext context) {
        jars.forEach(j -> {
            try {
                Set<String> classes = Jar.listClasses(j);

                for(String cls:classes) {
                    // if provided only included package are allowed
                    if (context.getIncludedPackages() != null && !context.getIncludedPackages().isEmpty()
                            && !context.getIncludedPackages().stream().anyMatch(p -> cls.startsWith(p))) {
                        logger.debug("Class rejected, package not included : {}", cls);
                        continue;
                    }

                    // if provided excluded package removed
                    if (context.getExcludedPackages() != null && !context.getExcludedPackages().isEmpty()
                            && context.getExcludedPackages().stream().anyMatch(p -> cls.startsWith(p))) {
                        logger.debug("Class rejected, package excluded : {}", cls);
                        continue;
                    }

                    try {
                        logger.debug("Class did pass package checks, continue : {}", cls);
                        Class<?> clazz = Class.forName(cls);
                        processClass(clazz, context);

                        for (Class<?> innerClass:clazz.getDeclaredClasses()) {
                            if (Modifier.isStatic(innerClass.getModifiers())
                                    && Modifier.isPublic(innerClass.getModifiers())) {
                                logger.debug("Inner Class did pass package checks, continue : {}", innerClass);
                                processClass(innerClass, context);
                            }
                        }
                    } catch (Throwable e) {
                    	logger.error("Unable to process class", e);
                        Report.error(cls, "Unable to process class", e);
                    }
                }
            } catch (IOException e) {
                Report.error("Unexpected exception occured while processing classes", e);
            }
        });
    }

    private void processClass(Class<?> cls, SearchContext context) {

        boolean accepted = context.getRootClasses().stream()
        		.anyMatch(rc -> rc.isAssignableFrom(cls));

        if (accepted) {
            logger.debug("Class has a root class into her hierarchy : {}", cls);
        }

        boolean excluded = context.getExcludeClasses().stream()
        		.anyMatch(rc -> rc.isAssignableFrom(cls));

        if (excluded) {
            logger.debug("Class does not have an excluded class into her hierarchy : {}", cls);
        }

        if (accepted && !excluded) {
            logger.info("Class did pass all checks, processing : {}", cls);

            BeanMetaData<?> btm = new BeanMetaData<>(cls, context.getAltConstructors());
            classes.put(cls, btm);
        }

    }

	public Map<Class<?>, BeanMetaData<?>> getClasses() {
		return classes;
	}


}
