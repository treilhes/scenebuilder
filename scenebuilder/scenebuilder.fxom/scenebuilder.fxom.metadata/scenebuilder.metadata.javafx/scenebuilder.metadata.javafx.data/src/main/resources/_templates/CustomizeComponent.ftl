<#if component.customization??>
    <#assign customizationClassName = context.componentCustomizationClassName!"<targetComponentCustomizationClass> not set in plugin configuration!">
    ${customizationClassName}.builder()
    <#if component.customization.labelMutation??>
        .labelMutation((originalLabel, object) -> ${component.customization.labelMutation})
    </#if>
        .resizeNeededWhenTopElement(${component.customization.resizeNeededWhenTop})
    <#if component.qualifiers??>
        <#list component.qualifiers as name, qualifier>
        .qualifier("${name}", ${customizationClassName}.Qualifier.builder()
            <#if qualifier.applicabilityCheck??> 
                .applicabilityCheck(${qualifier.applicabilityCheck})
            </#if>
                .category("${component.customization.category}")
                .description("")
                .fxmlUrl(getClass().getResource("${qualifier.fxml}"))
                .iconUrl(getClass().getResource("${qualifier.image}"))
                .iconX2Url(getClass().getResource("${qualifier.imagex2}"))
            <#if qualifier.label??>
                .label("${qualifier.label}")
            </#if>
                .build())
        </#list>
    </#if>
        .build()
    
<#else>
    ${logger.warn(component.metadata.type + " : Customization null.")}
    null
</#if>