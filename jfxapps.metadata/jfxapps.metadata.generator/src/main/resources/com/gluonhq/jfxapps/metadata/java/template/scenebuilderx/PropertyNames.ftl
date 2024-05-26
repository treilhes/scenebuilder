package ${context.targetPackage};

import com.gluonhq.jfxapps.core.fxom.util.PropertyName;

/*
 * THIS CODE IS AUTOMATICALLY GENERATED !
 */
public interface PropertyNames {

<#list properties as name, property>
    public final PropertyName ${name}Name = new PropertyName("${property.metadata.name}"<#if property.metadata.static == true>, ${property.metadata.residenceClass.name}.class</#if>); //NOCHECK
</#list>
}
