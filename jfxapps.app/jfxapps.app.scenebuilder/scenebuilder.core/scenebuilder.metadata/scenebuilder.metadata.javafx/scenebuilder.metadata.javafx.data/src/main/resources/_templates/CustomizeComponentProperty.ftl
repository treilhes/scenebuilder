<#if property.customization??>
    <#assign customizationClassName = context.componentPropertyCustomizationClassName!"<targetComponentPropertyCustomizationClass> not set in plugin configuration!">
    ${customizationClassName}.builder()
        <#if property.customization.image??>
        .iconUrl(getClass().getResource("${property.customization.image}"))
        </#if>
        <#if property.customization.imagex2??>
        .iconX2Url(getClass().getResource("${property.customization.imagex2}"))
        </#if>
        .order(${property.customization.order?c})
        .resizeNeededWhenTopElement(${property.customization.freeChildPositioning})
        .build()
<#else>
    ${logger.warn(property.metadata.type + " : Customization null.")}
    null
</#if>
