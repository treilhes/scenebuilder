<#if property.customization??>
    <#assign customizationClassName = context.valuePropertyCustomizationClassName!"<targetValuePropertyCustomizationClass> not set in plugin configuration!">
    ${customizationClassName}.builder()
        .inspectorPath(${customizationClassName}.InspectorPath.builder()
    <#if property.customization.section??>
            .sectionTag("${property.customization.section}")
    </#if>
    <#if property.customization.subSection??>
            .subSectionTag("${property.customization.subSection}")
    </#if>
    <#if property.customization.order??>
            .subSectionIndex(${property.customization.order?c})
    </#if>
            .build())
        .build()
<#else>
    ${logger.warn(property.metadata.type + " : Customization null.")}
    null
</#if>
