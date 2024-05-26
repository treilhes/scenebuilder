                    .order(${property.customization.order?c})
                    <#if property.customization.image??>.iconUrl(getClass().getResource("${property.customization.image}"))</#if>
                    <#if property.customization.imageX2??>.iconX2Url(getClass().getResource("${property.customization.imageX2}"))</#if>
                    
            
                    a executer sur le composant lui meme
                            
                    <#if property.customization.freeChildPositioning == true>
            setFreeChildPositioning(${property.memberName}PropertyMetadata, true);
            </#if>
            <#if property.customization.childLabelMutation??>
            setChildLabelMutation(${property.memberName}PropertyMetadata, (originalLabel, object, child) -> ${property.customization.childLabelMutation});
            </#if>