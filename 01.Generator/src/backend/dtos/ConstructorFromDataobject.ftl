<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symTab", "dtoClassName", "originalName", "hasSuperclass", "attributes", "genericAttributes")}
<#assign getterSetterHelper = tc.instantiate("common.util.GetterSetterHelper")>
<#assign typeHelper = tc.instantiate("common.util.TypeHelper")>
<#assign utils = tc.instantiate("common.util.TransformationUtils")>

{
<#if hasSuperclass>
    super(${originalName?uncap_first});
</#if>

    this.typeName = "${dtoClassName}";
    this.setId(${originalName?uncap_first}.getId());

<#list genericAttributes as gAttr>
    <#if typeHelper.isOptional(gAttr.getType())>
        <#assign default=typeHelper.getDefaultValue(gAttr.getType().getName())>
        this.${gAttr.getName()} = ${originalName?uncap_first}.${getterSetterHelper.getPlainGetter(gAttr)}().orElse(${default});
    <#elseif utils.isDTOType(symTab, gAttr.getType())>
        this.${gAttr.getName()} = new ${gAttr.getType()}DTO(${originalName?uncap_first}.${getterSetterHelper.getPlainGetter(gAttr)}());
    <#else>
        this.${gAttr.getName()} = ${originalName?uncap_first}.${getterSetterHelper.getPlainGetter(gAttr)}();
    </#if>
</#list>

<#list attributes as attribute>
    <#if typeHelper.isOptional(attribute.getType())>
        <#assign default=typeHelper.getDefaultValue(attribute.getType().getName())>
        this.${attribute.getName()} = ${originalName?uncap_first}.${getterSetterHelper.getPlainGetter(attribute)}().orElse(${default});
    <#elseif utils.isDTOType(symTab, attribute.getType())>
        this.${attribute.getName()} = new ${attribute.getType()}DTO(${originalName?uncap_first}.${getterSetterHelper.getPlainGetter(attribute)}());
    <#else>
        this.${attribute.getName()} = ${originalName?uncap_first}.${getterSetterHelper.getPlainGetter(attribute)}();
    </#if>
</#list>

}
