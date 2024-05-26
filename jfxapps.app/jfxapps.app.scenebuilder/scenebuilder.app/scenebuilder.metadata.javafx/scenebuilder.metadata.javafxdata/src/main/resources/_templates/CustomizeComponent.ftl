<#if component.customization??>
    null
<#else>
    ${logger.warn(component.metadata.type + " : Customization null.")}
    null
</#if>