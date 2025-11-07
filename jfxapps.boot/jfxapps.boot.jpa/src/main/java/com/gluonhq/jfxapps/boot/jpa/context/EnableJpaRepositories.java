/*
 * Copyright (c) 2016, 2025, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2025, Pascal Treilhes and/or its affiliates.
 * Copyright (c) 2012, 2014, Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation and Gluon nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.gluonhq.jfxapps.boot.jpa.context;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.query.QueryEnhancerSelector;
import org.springframework.data.repository.config.BootstrapMode;
import org.springframework.data.repository.config.DefaultRepositoryBaseClass;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryLookupStrategy.Key;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManagerFactory;

/**
 * This annotation does not trigger repository scan contrary to {@link org.springframework.data.jpa.repository.config.EnableJpaRepositories}
 * due to the removed import JpaRepositoriesRegistrar from the annotation.
 * All values used for scanning like basePackages, basePackageClasses, includeFilters, excludeFilters are ignored.
 *
 * @author Oliver Gierke
 * @author Thomas Darimont
 * @author Greg Turnquist
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableJpaRepositories {

    /**
     * Alias for the {@link #basePackages()} attribute. Allows for more concise annotation declarations e.g.:
     * {@code @EnableJpaRepositories("org.my.pkg")} instead of {@code @EnableJpaRepositories(basePackages="org.my.pkg")}.
     */
    String[] value() default {};

    /**
     * Base packages to scan for annotated components. {@link #value()} is an alias for (and mutually exclusive with) this
     * attribute. Use {@link #basePackageClasses()} for a type-safe alternative to String-based package names.
     */
    String[] basePackages() default {};

    /**
     * Type-safe alternative to {@link #basePackages()} for specifying the packages to scan for annotated components. The
     * package of each class specified will be scanned. Consider creating a special no-op marker class or interface in
     * each package that serves no purpose other than being referenced by this attribute.
     */
    Class<?>[] basePackageClasses() default {};

    /**
     * Specifies which types are eligible for component scanning. Further narrows the set of candidate components from
     * everything in {@link #basePackages()} to everything in the base packages that matches the given filter or filters.
     */
    Filter[] includeFilters() default {};

    /**
     * Specifies which types are not eligible for component scanning.
     */
    Filter[] excludeFilters() default {};

    /**
     * Returns the postfix to be used when looking up custom repository implementations. Defaults to {@literal Impl}. So
     * for a repository named {@code PersonRepository} the corresponding implementation class will be looked up scanning
     * for {@code PersonRepositoryImpl}.
     *
     * @return
     */
    String repositoryImplementationPostfix() default "Impl";

    /**
     * Configures the location of where to find the Spring Data named queries properties file. Will default to
     * {@code META-INF/jpa-named-queries.properties}.
     *
     * @return
     */
    String namedQueriesLocation() default "";

    /**
     * Returns the key of the {@link QueryLookupStrategy} to be used for lookup queries for query methods. Defaults to
     * {@link Key#CREATE_IF_NOT_FOUND}.
     *
     * @return
     */
    Key queryLookupStrategy() default Key.CREATE_IF_NOT_FOUND;

    /**
     * Returns the {@link FactoryBean} class to be used for each repository instance. Defaults to
     * {@link JfxAppsJpaRepositoryFactoryBean}.
     *
     * @return
     */
    Class<?> repositoryFactoryBeanClass() default JfxAppsJpaRepositoryFactoryBean.class;

    /**
     * Configure the repository base class to be used to create repository proxies for this particular configuration.
     *
     * @return
     * @since 1.9
     */
    Class<?> repositoryBaseClass() default DefaultRepositoryBaseClass.class;

    /**
     * Configure a specific {@link BeanNameGenerator} to be used when creating the repository beans.
     * @return the {@link BeanNameGenerator} to be used or the base {@link BeanNameGenerator} interface to indicate context default.
     * @since 3.4
     */
    Class<? extends BeanNameGenerator> nameGenerator() default BeanNameGenerator.class;

    // JPA specific configuration

    /**
     * Configures the name of the {@link EntityManagerFactory} bean definition to be used to create repositories
     * discovered through this annotation. Defaults to {@code entityManagerFactory}.
     *
     * @return
     */
    String entityManagerFactoryRef() default "entityManagerFactory";

    /**
     * Configures the name of the {@link PlatformTransactionManager} bean definition to be used to create repositories
     * discovered through this annotation. Defaults to {@code transactionManager}.
     *
     * @return
     */
    String transactionManagerRef() default "transactionManager";

    /**
     * Configures whether nested repository-interfaces (e.g. defined as inner classes) should be discovered by the
     * repositories infrastructure.
     */
    boolean considerNestedRepositories() default false;

    /**
     * Configures whether to enable default transactions for Spring Data JPA repositories. Defaults to {@literal true}. If
     * disabled, repositories must be used behind a facade that's configuring transactions (e.g. using Spring's annotation
     * driven transaction facilities) or repository methods have to be used to demarcate transactions.
     *
     * @return whether to enable default transactions, defaults to {@literal true}.
     */
    boolean enableDefaultTransactions() default true;

    /**
     * Configures when the repositories are initialized in the bootstrap lifecycle. {@link BootstrapMode#DEFAULT}
     * (default) means eager initialization except all repository interfaces annotated with {@link Lazy},
     * {@link BootstrapMode#LAZY} means lazy by default including injection of lazy-initialization proxies into client
     * beans so that those can be instantiated but will only trigger the initialization upon first repository usage (i.e a
     * method invocation on it). This means repositories can still be uninitialized when the application context has
     * completed its bootstrap. {@link BootstrapMode#DEFERRED} is fundamentally the same as {@link BootstrapMode#LAZY},
     * but triggers repository initialization when the application context finishes its bootstrap.
     *
     * @return
     * @since 2.1
     */
    BootstrapMode bootstrapMode() default BootstrapMode.DEFAULT;

    /**
     * Configures what character is used to escape the wildcards {@literal _} and {@literal %} in derived queries with
     * {@literal contains}, {@literal startsWith} or {@literal endsWith} clauses.
     *
     * @return a single character used for escaping.
     */
    char escapeCharacter() default '\\';


    /**
     * Configures the {@link QueryEnhancerSelector} to select a query enhancer for query introspection and transformation.
     *
     * @return a {@link QueryEnhancerSelector} class providing a no-args constructor.
     * @since 4.0
     */
    Class<? extends QueryEnhancerSelector> queryEnhancerSelector() default QueryEnhancerSelector.DefaultQueryEnhancerSelector.class;
}