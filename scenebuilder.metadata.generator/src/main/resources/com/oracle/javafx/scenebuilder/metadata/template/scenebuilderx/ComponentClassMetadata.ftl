package ${package};

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import ${component.custom["propertyNamesClass"]};

import com.oracle.javafx.scenebuilder.core.metadata.klass.ComponentClassMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.ComponentPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.util.InspectorPath;

/*
 * THIS CODE IS AUTOMATICALLY GENERATED !
 */
@Component
public class ${metadataPrefix}${component.raw.type.simpleName}Metadata extends ComponentClassMetadata<${component.raw.type.name?replace("$", ".")}> {

<#list properties as property>
<#if property.type == "VALUE">
    public final ${property.raw.metadataClass.name?replace("$", ".")} ${property.custom["memberName"]}PropertyMetadata =
            new ${property.raw.metadataClass.name?replace("$", ".")}.Builder<#if property.raw.type.enum == true><>(${property.raw.type.name?replace("$", ".")}.class)<#else>()</#if>
                .withName(PropertyNames.${property.custom["memberName"]}Name)
                .withReadWrite(${property.raw.readWrite})
    <#if property.custom["nullEquivalent"]??>
                .withNullEquivalent("${property.custom["nullEquivalent"]}")
	</#if>
	<#if property.custom["defaultValue"]??>
				.withDefaultValue(${property.custom["defaultValue"]})
	</#if>
                .withInspectorPath(new InspectorPath("${property.raw.section}", "${property.raw.subSection}", ${property.raw.subSectionIndex}))
                .build();
</#if>
<#if property.type == "COMPONENT">
    private final ComponentPropertyMetadata ${property.raw.name}PropertyMetadata;
</#if>
</#list>

    protected ${metadataPrefix}${component.raw.type.simpleName}Metadata(<#if component.parent??>@Autowired ${component.parent.custom["className"]} parent</#if>
			<#list metadataComponents as cmp>
			<#if cmp?index != 0 || component.parent??>, </#if>@Lazy @Autowired ${cmp.custom["className"]?replace("$", ".")} ${cmp.raw.type.simpleName?uncap_first}Metadata
			</#list>
    	) {
        super(${component.raw.type.name?replace("$", ".")}.class, <#if component.parent??>parent<#else>null</#if>);
        setResizeNeededWhenTopElement(${component.raw.resizeNeededWhenTopElement});

        <#list properties as property>
		<#if property.type == "COMPONENT">
		    ${property.raw.name}PropertyMetadata = new ComponentPropertyMetadata(
	                PropertyNames.${property.custom["memberName"]}Name,
	                <#if property.raw.collection == true>${property.raw.collectionType.simpleName?uncap_first}<#else>${property.raw.type.simpleName?uncap_first}</#if>Metadata,
	                ${property.raw.collection}, /* collection */
	                <#if property.raw.image??>getClass().getResource("${property.raw.image}")<#else>null</#if>,
	                <#if property.raw.imageX2??>getClass().getResource("${property.raw.imageX2}")<#else>null</#if>,
	                <#if component.raw.defaultProperty?? && component.raw.defaultProperty.name == property.raw.name>true<#else>false</#if>,
	                ${property.raw.subSectionIndex}
	                );
		</#if>
		</#list>

        <#list properties as property>
        getProperties().add(${property.custom["memberName"]}PropertyMetadata);
        <#if property.raw.freeChildPositioning == true>
        setFreeChildPositioning(${property.custom["memberName"]}PropertyMetadata, true);

        </#if>
        </#list>

        <#list component.raw.qualifiers as qualifier>
        getQualifiers().put("${qualifier.name}",
                new Qualifier(
                        <#if qualifier.fxml??>getClass().getResource("${qualifier.fxml}")<#else>null</#if>,
                        "${qualifier.name}",
                        "${component.raw.version}",
                        <#if qualifier.image??>getClass().getResource("${qualifier.image}")<#else>null</#if>,
                        <#if qualifier.imageX2??>getClass().getResource("${qualifier.imageX2}")<#else>null</#if>,
                        "${component.raw.category}"
                        <#if qualifier.lambdaCheck??>, ${qualifier.lambdaCheck}</#if>
                        ));
        </#list>

    }
}