setResizeNeededWhenTopElement(${component.customization.resizeNeededWhenTop});

<#if component.customization.descriptionProperty??>
setDescriptionProperty(${component.customization.descriptionProperty}PropertyMetadata);
</#if>
<#if component.customization.labelMutation??>
setLabelMutation((originalLabel, object) -> ${component.customization.labelMutation});
</#if>