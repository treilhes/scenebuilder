package com.gluonhq.jfxapps.boot.api.aop;

import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.data.util.TypeInformation;

public interface ScanMetadata {

    TypeInformation<?> getTypeInformation();

    Class<?> getClassHolder();

    boolean hasScanAnnotation();

    String[] getValue();

    Class<?>[] getBasePackageClasses();

    String[] getBasePackages();

    Filter[] getExcludeFilters();

    Filter[] getIncludeFilters();

}