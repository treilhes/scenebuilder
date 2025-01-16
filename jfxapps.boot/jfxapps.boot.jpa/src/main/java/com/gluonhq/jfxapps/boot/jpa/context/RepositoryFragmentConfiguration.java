package com.gluonhq.jfxapps.boot.jpa.context;

import java.beans.Introspector;
import java.util.Optional;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.data.config.ConfigurationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

class RepositoryFragmentConfiguration {

    private final Optional<String> interfaceName;
    private final String className;
    private final Optional<AbstractBeanDefinition> beanDefinition;
    private final String beanName;

    /**
     * Creates a {@link RepositoryFragmentConfiguration} given {@code interfaceName} and {@code className} of the
     * implementation.
     *
     * @param interfaceName
     * @param className must not be {@literal null} or empty.
     */
    public RepositoryFragmentConfiguration(@Nullable String interfaceName, String className) {
        this(interfaceName, className, Optional.empty(), generateBeanName(className));
    }

    /**
     * Creates a {@link RepositoryFragmentConfiguration} given {@code interfaceName} and {@link AbstractBeanDefinition} of
     * the implementation.
     *
     * @param interfaceName
     * @param beanDefinition must not be {@literal null}.
     */
    public RepositoryFragmentConfiguration(@Nullable String interfaceName, AbstractBeanDefinition beanDefinition) {

        Assert.notNull(beanDefinition, "Bean definition must not be null");

        this.interfaceName = Optional.ofNullable(interfaceName);
        this.className = ConfigurationUtils.getRequiredBeanClassName(beanDefinition);
        this.beanDefinition = Optional.of(beanDefinition);
        this.beanName = generateBeanName();
    }

    RepositoryFragmentConfiguration(@Nullable String interfaceName, AbstractBeanDefinition beanDefinition,
            String beanName) {
        this(interfaceName, ConfigurationUtils.getRequiredBeanClassName(beanDefinition), Optional.of(beanDefinition),
                beanName);
    }

    private RepositoryFragmentConfiguration(@Nullable String interfaceName, String className,
            Optional<AbstractBeanDefinition> beanDefinition, String beanName) {

        Assert.notNull(beanDefinition, "Bean definition must not be null");
        Assert.notNull(beanName, "Bean name must not be null");

        this.interfaceName = Optional.ofNullable(interfaceName);
        this.className = className;
        this.beanDefinition = beanDefinition;
        this.beanName = beanName;
    }

    private String generateBeanName() {
        return generateBeanName(getClassName());
    }

    private static String generateBeanName(String className) {
        return Introspector.decapitalize(ClassUtils.getShortName(className));
    }

    /**
     * @return name of the implementation bean.
     */
    public String getImplementationBeanName() {
        return this.beanName;
    }

    /**
     * @return name of the implementation fragment bean.
     */
    public String getFragmentBeanName() {
        return getImplementationBeanName() + "Fragment";
    }

    @Nullable
    public String getInterfaceName() {
        return this.interfaceName.orElse(null);
    }

    public String getClassName() {
        return this.className;
    }

    public Optional<AbstractBeanDefinition> getBeanDefinition() {
        return this.beanDefinition;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof RepositoryFragmentConfiguration that)) {
            return false;
        }

        if (!ObjectUtils.nullSafeEquals(interfaceName, that.interfaceName)) {
            return false;
        }

        if (!ObjectUtils.nullSafeEquals(className, that.className)) {
            return false;
        }

        return ObjectUtils.nullSafeEquals(beanDefinition, that.beanDefinition);
    }

    @Override
    public int hashCode() {
        int result = ObjectUtils.nullSafeHashCode(interfaceName);
        result = 31 * result + ObjectUtils.nullSafeHashCode(className);
        result = 31 * result + ObjectUtils.nullSafeHashCode(beanDefinition);
        return result;
    }

    @Override
    public String toString() {
        return "RepositoryFragmentConfiguration(interfaceName=" + this.getInterfaceName() + ", className="
                + this.getClassName() + ", beanDefinition=" + this.getBeanDefinition() + ")";
    }
}