package ${package};

import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;

/*
 * THIS CODE IS AUTOMATICALLY GENERATED !
 */
public interface PropertyNames {

<#list properties as property>
    public final PropertyName ${property.custom["memberName"]}Name =  new PropertyName("${property.raw.name}"<#if property.raw.static == true>, ${property.raw.residenceClass.name}.class</#if>); //NOCHECK
</#list>

}
