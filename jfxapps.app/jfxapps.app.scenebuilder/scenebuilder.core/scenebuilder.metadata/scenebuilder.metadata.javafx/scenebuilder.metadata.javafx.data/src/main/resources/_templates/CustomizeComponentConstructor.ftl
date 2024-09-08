<#if component.customization??>
    <#if component.customization.descriptionProperty??>
    getCustomization().setDescriptionProperty(${component.customization.descriptionProperty}PropertyMetadata);
    </#if>
<#else>
    ${logger.warn(component.metadata.type + " : Customization null.")}
</#if>

<#list component.componentProperties as key, property>
    <#if property.metadataClass??>
        <#if property.customization??>
            <#if property.customization.freeChildPositioning>
                getCustomization().setFreeChildPositioning(${property.memberName}PropertyMetadata, ${property.customization.freeChildPositioning});
            </#if>
        </#if>
    <#else>
        ${logger.info("MetadataClass not set! Discard component property " + component.metadata.name + "." + property.metadata.name + " : " + property.metadata.contentType)}
    </#if>
</#list>