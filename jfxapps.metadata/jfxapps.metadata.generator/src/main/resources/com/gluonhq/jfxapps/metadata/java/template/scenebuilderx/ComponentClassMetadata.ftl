<#assign componentClassName = component.metadata.type.name?replace("$", ".")>
<#assign componentSuperClassName = context.componentSuperClassName!"<targetComponentSuperClass> not set in plugin configuration!">
package ${component.metadataClassPackage};

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import ${context.targetPackage}.${context.propertyNamesClassSimpleName};

import com.gluonhq.jfxapps.core.metadata.klass.ComponentClassMetadata;
import com.gluonhq.jfxapps.core.metadata.property.ComponentPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.PropertyMetadata.Visibility;

/*
 * THIS CODE IS AUTOMATICALLY GENERATED !
 */
@Component
public class ${component.metadataClassSimpleName} extends ${componentSuperClassName}<${componentClassName}> {

<#macro valuePropertyMacro property customization>
    <#assign propertyMetadataClassName = property.metadataClass.name?replace("$", ".")>
    <#assign genericCusto = context.valuePropertyCustomizationClassName!"<targetValuePropertyCustomizationClass> not set in plugin configuration!">
    <#assign contentClass = property.metadata.type.name?replace("$", ".")>

    public static final ${propertyMetadataClassName}<${genericCusto}> 
        ${property.memberName}PropertyMetadata =
    <#if property.metadata.type.enum == true>
            new ${propertyMetadataClassName}.Builder<${contentClass}, ${genericCusto}>(${contentClass}.class)
    <#else>
            new ${propertyMetadataClassName}.Builder<${genericCusto}>()
    </#if>
                .name(PropertyNames.${property.memberName}Name)
                .readWrite(${property.metadata.readWrite})
    <#if !property.defaultValue?? && property.nullEquivalent??>
                .nullEquivalent("${property.nullEquivalent}")
    </#if>
    <#if !property.nullEquivalent?? && property.defaultValue??>
                .defaultValue(${property.defaultValue})
    </#if>
                .customization(${customization!"null"})
                .build();
</#macro>

        // local properties 
<#list component.valueProperties as key, property>
    <#if property.metadataClass??>
        ${logger.info("Processing value property " + component.metadata.type + "." + property.metadata.name + " : " + property.metadata.contentType)}
        <#assign custo = customization.customizeValueProperty(context, component, property)>
        <@valuePropertyMacro property custo/>
    <#else>
        <#assign errMsg = "MetadataClass not set! Discard value property " + component.metadata.type + "." + property.metadata.name + " : " + property.metadata.contentType>
        ${logger.warn(errMsg)}
        // ${errMsg}
    </#if>
</#list>

        // static properties
<#list component.staticValueProperties as key, property>
    <#if property.metadataClass??>
        ${logger.info("Processing value property " + component.metadata.type + "." + property.metadata.name + " : " + property.metadata.contentType)}
        <#assign custo = customization.customizeStaticValueProperty(context, component, property)>
        <@valuePropertyMacro property custo/>
    <#else>
        <#assign errMsg = "MetadataClass not set! Discard static value property " + component.metadata.type + "." + property.metadata.name + " : " + property.metadata.contentType>
        ${logger.warn(errMsg)}
        // ${errMsg}
    </#if>
</#list>
        // updated properties
<#list component.updatedValueProperties as key, property>
    <#if property.metadataClass??>
        ${logger.info("Processing updated property " + component.metadata.type + "." + property.metadata.name + " : " + property.metadata.contentType)}
        <#assign custo = customization.customizeValueProperty(context, component, property)>
        <@valuePropertyMacro property custo/>
    <#else>
        <#assign errMsg = "MetadataClass not set! Discard updated value property " + component.metadata.type + "." + property.metadata.name + " : " + property.metadata.contentType>
        ${logger.warn(errMsg)}
        // ${errMsg}
    </#if>
</#list>

<#list component.componentProperties as key, property>
    <#if property.metadataClass??>
        <#assign propertyMetadataClassName = property.metadataClass.name?replace("$", ".")>
        ${logger.info("Processing component property " + component.metadata.name + "." + property.metadata.name + " : " + property.metadata.contentType)}
        private final ${propertyMetadataClassName}<${context.componentPropertyCustomizationClassName!"<targetComponentPropertyCustomizationClass> not set in plugin configuration!"}> 
            ${property.memberName}PropertyMetadata;
    <#else>
        ${logger.info("MetadataClass not set! Discard component property " + component.metadata.name + "." + property.metadata.name + " : " + property.metadata.contentType)}
    </#if>
</#list>

    protected ${component.metadataClassSimpleName}(
    <#if component.parent??>
        ${component.parent.metadataClassName} parent
    </#if>
    <#list component.componentDependencies as cls, dependencyMetadataClassName>
        <#if cls?index != 0 || component.parent??>, </#if>
        @Lazy ${dependencyMetadataClassName} ${cls.simpleName?uncap_first}Metadata
    </#list>
    )
    {
        super(${componentClassName}.class, <#if component.parent??>parent<#else>null</#if>, ${customization.customizeComponent(context, component)!"null"});

        var ${component.metadata.type.simpleName?uncap_first}Metadata = this;
        
        <#list component.componentProperties as key, property>
            <#if property.metadataClass??>
            ${logger.info("Processing 2 " + component.metadata.name + "." + property.metadata.name + " : " + property.metadata.contentType)}
            <#assign propertyMetadataClassName = property.metadataClass.name?replace("$", ".")>
            
            ${property.memberName}PropertyMetadata = new ${propertyMetadataClassName}.Builder<${context.componentPropertyCustomizationClassName!"<targetComponentPropertyCustomizationClass> not set in plugin configuration!"}>()
                    .name(PropertyNames.${property.memberName}Name)
                    .classMetadata(<#if property.metadata.collection == true>${property.metadata.collectionType.simpleName?uncap_first}<#else>${property.metadata.type.simpleName?uncap_first}</#if>Metadata)
                    .isCollection(${property.metadata.collection})
                    <#if property.metadata.main??>.isMain(${property.metadata.main})<#else>.isMain(<#if component.metadata.defaultProperty?? && component.metadata.defaultProperty.name == property.metadata.name>true<#else>false</#if>)</#if>
                    .customization(${customization.customizeComponentProperty(context, component, property)!"null"})
                    .build();
                    
            getProperties().add(${property.memberName}PropertyMetadata);
            <#else>
                <#assign errMsg = "MetadataClass not set! Discard component property " + component.metadata.type + "." + property.metadata.name + " : " + property.metadata.contentType>
                ${logger.warn(errMsg)}
                // ${errMsg}
            </#if>
        </#list>

        <#list component.staticValueProperties as key, property>
            <#if property.metadataClass??>
                ${logger.info("Processing 2 " + component.metadata.name + "." + property.metadata.name + " : " + property.metadata.contentType)}
                ${property.metadata.applicability.simpleName?uncap_first}Metadata.getProperties().add(${property.memberName}PropertyMetadata);
            </#if>
        </#list>
        
        <#list component.valueProperties as key, property>
            <#if property.metadataClass??>
                ${logger.info("Processing 2 " + component.metadata.name + "." + property.metadata.name + " : " + property.metadata.contentType)}
                getProperties().add(${property.memberName}PropertyMetadata);
            </#if>
        </#list>

        ${customization.customizeComponentConstructor(context, component)!""}

        <#list component.shadows as shadowed>
        getShadowedProperties().add(PropertyNames.${shadowed}Name);
        </#list>

    }
}