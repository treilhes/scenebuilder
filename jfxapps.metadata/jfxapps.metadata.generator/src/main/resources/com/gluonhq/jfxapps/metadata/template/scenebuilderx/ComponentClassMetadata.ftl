package ${package};

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import ${component.custom["propertyNamesClass"]};

import com.oracle.javafx.scenebuilder.core.metadata.klass.ComponentClassMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.ComponentPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.PropertyMetadata.Visibility;
import com.oracle.javafx.scenebuilder.core.metadata.util.InspectorPath;

/*
 * THIS CODE IS AUTOMATICALLY GENERATED !
 */
@Component
public class ${metadataPrefix}${component.raw.type.simpleName}Metadata extends ComponentClassMetadata<${component.raw.type.name?replace("$", ".")}> {

<#list properties as property>${logger.info("Processing 1 " + component.raw.name + "." + property.raw.name + " : " + property.type)}
<#if property.type == "VALUE">
    public static final ${property.raw.metadataClass.name?replace("$", ".")} ${property.custom["memberName"]}PropertyMetadata =
            new ${property.raw.metadataClass.name?replace("$", ".")}.Builder<#if property.raw.type.enum == true><>(${property.raw.type.name?replace("$", ".")}.class)<#else>()</#if>
                .withName(PropertyNames.${property.custom["memberName"]}Name)
                .withReadWrite(${property.raw.readWrite})
    <#if property.custom["defaultValue"] == "null" && property.raw.nullEquivalent??>
                .withNullEquivalent("${property.raw.nullEquivalent}")
	</#if>
	<#if !property.raw.nullEquivalent?? && property.custom["defaultValue"]??>
				.withDefaultValue(${property.custom["defaultValue"]})
	</#if>
                .withInspectorPath(new InspectorPath("${property.raw.section}", "${property.raw.subSection}", ${property.raw.order?c}))
                .withVisibility(Visibility.${property.raw.visibility.name()})
                .build();
</#if>
<#if property.type == "COMPONENT">
    private final ${property.raw.metadataClass.name?replace("$", ".")} ${property.raw.name}PropertyMetadata;
</#if>
</#list>

    protected ${metadataPrefix}${component.raw.type.simpleName}Metadata(<#if component.parent??>@Autowired ${component.parent.custom["className"]} parent</#if>
			<#list metadataComponents as cmp>
			<#if cmp?index != 0 || component.parent??>, </#if>@Lazy @Autowired ${cmp.custom["className"]?replace("$", ".")} ${cmp.raw.type.simpleName?uncap_first}Metadata
			</#list>
    	) {
        super(${component.raw.type.name?replace("$", ".")}.class, <#if component.parent??>parent<#else>null</#if>);
        setResizeNeededWhenTopElement(${component.raw.resizeNeededWhenTopElement});

        <#list properties as property>${logger.info("Processing 2 " + component.raw.name + "." + property.raw.name + " : " + property.type)}
		<#if property.type == "COMPONENT">
		    ${property.raw.name}PropertyMetadata = new ${property.raw.metadataClass.name?replace("$", ".")}.Builder()
	                .withName(PropertyNames.${property.custom["memberName"]}Name)
	                .withClassMetadata(<#if property.raw.collection == true>${property.raw.collectionType.simpleName?uncap_first}<#else>${property.raw.type.simpleName?uncap_first}</#if>Metadata)
	                .withIsCollection(${property.raw.collection})
	                <#if property.raw.image??>.withIconUrl(getClass().getResource("${property.raw.image}"))</#if>
	                <#if property.raw.imageX2??>.withIconX2Url(getClass().getResource("${property.raw.imageX2}"))</#if>
	                <#if property.raw.main??>.withIsMain(${property.raw.main})<#else>.withIsMain(<#if component.raw.defaultProperty?? && component.raw.defaultProperty.name == property.raw.name>true<#else>false</#if>)</#if>
	                .withOrder(${property.raw.order?c})
	                .withVisibility(Visibility.${property.raw.visibility.name()})
	                .build();
		</#if>
		</#list>

		<#if component.raw.descriptionProperty??>
		setDescriptionProperty(${component.raw.descriptionProperty}PropertyMetadata);
        </#if>
		<#if component.raw.labelMutation??>
		setLabelMutation((originalLabel, object) -> ${component.raw.labelMutation});
        </#if>

        <#list properties as property>
        getProperties().add(${property.custom["memberName"]}PropertyMetadata);
        <#if property.raw.freeChildPositioning == true>
        setFreeChildPositioning(${property.custom["memberName"]}PropertyMetadata, true);
        </#if>
        <#if property.raw.childLabelMutation??>
        setChildLabelMutation(${property.custom["memberName"]}PropertyMetadata, (originalLabel, object, child) -> ${property.raw.childLabelMutation});
        </#if>
        </#list>

        <#list component.raw.shadows as shadowed>
        getShadowedProperties().add(PropertyNames.${shadowed}Name);
        </#list>

        <#list component.raw.qualifiers as qualifier>
        getQualifiers().put("${qualifier.name}",
                new Qualifier(
                        <#if qualifier.fxml??>getClass().getResource("${qualifier.fxml}")<#else>null</#if>,
                        <#if qualifier.label??>"${qualifier.label}"<#else>"${qualifier.name}"</#if>,
                        "${component.raw.version}",
                        <#if qualifier.image??>getClass().getResource("${qualifier.image}")<#else>null</#if>,
                        <#if qualifier.imageX2??>getClass().getResource("${qualifier.imageX2}")<#else>null</#if>,
                        "${component.raw.category}"
                        <#if qualifier.lambdaCheck??>, ${qualifier.lambdaCheck}</#if>
                        ));
        </#list>

    }
}