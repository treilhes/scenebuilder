package com.oracle.javafx.scenebuilder.metadata.finder;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import com.oracle.javafx.scenebuilder.metadata.finder.api.Executor;

public class SearchContext {
    private List<Class<?>> rootClasses = new ArrayList<>();
    private List<Class<?>> excludeClasses = new ArrayList<>();
    private List<Pattern> jarFilterPatterns = new ArrayList<>();
    private List<String> includedPackages = new ArrayList<>();
    private List<String> excludedPackages = new ArrayList<>();
    private Map<Constructor<?>, Class[]> altConstructors = new HashMap<>();
    private File sourceFolder;
    private File resourceFolder;
    private String targetPackage;
    private Class<Executor> executorClass;
    private String extensionName;
    private String moduleName;
    private String metadataPrefix;
    private UUID uuid;
    private List<String> moduleRequires = new ArrayList<>();

    public List<Class<?>> getRootClasses() {
        return rootClasses;
    }
    public void addRootClass(Class<?> rootClass) {
        rootClasses.add(rootClass);
    }
    public List<Class<?>> getExcludeClasses() {
        return excludeClasses;
    }
    public void addExcludeClass(Class<?> excludeClass) {
        excludeClasses.add(excludeClass);
    }
    public List<Pattern> getJarFilterPatterns() {
        return jarFilterPatterns;
    }
    public void addJarFilterPattern(Pattern jarFilterPattern) {
        jarFilterPatterns.add(jarFilterPattern);
    }
    public List<String> getIncludedPackages() {
        return includedPackages;
    }
    public void addIncludedPackage(String includedPackage) {
        includedPackages.add(includedPackage);
    }
    public List<String> getExcludedPackages() {
        return excludedPackages;
    }
    public void addExcludedPackage(String excludedPackage) {
        excludedPackages.add(excludedPackage);
    }
    public Map<Constructor<?>, Class[]> getAltConstructors() {
        return altConstructors;
    }
    public void addAltConstructor(Constructor<?> constructor, Class<?>[] parameters) {
        altConstructors.put(constructor, parameters);
    }
    public File getSourceFolder() {
        return sourceFolder;
    }
    public void setSourceFolder(File outputFolder) {
        this.sourceFolder = outputFolder;
    }

    public File getResourceFolder() {
        return resourceFolder;
    }
    public void setResourceFolder(File resourceFolder) {
        this.resourceFolder = resourceFolder;
    }
    public String getTargetPackage() {
        return targetPackage;
    }
    public void setTargetPackage(String targetPackage) {
        this.targetPackage = targetPackage;
    }
    public Class<Executor> getExecutorClass() {
        return executorClass;
    }
    public void setExecutorClass(Class<Executor> executorClass) {
        this.executorClass = executorClass;
    }
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }
    public String getModuleName() {
        return moduleName;
    }
    public void addModuleRequire(String s) {
        moduleRequires.add(s);
    }
    public List<String> getModuleRequires() {
        return moduleRequires;
    }
    public UUID getUuid() {
        return uuid;
    }
    public void setUuid(String uuid) {
        this.uuid = UUID.fromString(uuid);
    }
    public String getExtensionName() {
        return extensionName;
    }
    public void setExtensionName(String extensionName) {
        this.extensionName = extensionName;
    }
    public String getMetadataPrefix() {
        return metadataPrefix;
    }
    public void setMetadataPrefix(String metadataPrefix) {
        this.metadataPrefix = metadataPrefix;
    }


}
