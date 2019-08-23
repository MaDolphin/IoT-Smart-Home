<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("originalName", "attributes", "assocs")}
<#assign getterSetterHelper = tc.instantiate("common.util.GetterSetterHelper")>
<#assign cdassoc = tc.instantiate("common.util.CDAssociationUtil")>
<#assign typeHelper = tc.instantiate("common.util.TypeHelper")>
<#assign assocNameUtil = tc.instantiate("backend.coretemplates.association.AssociationNameUtil")>

{
id = ${originalName?uncap_first}.getId();
<#list attributes as attribute>
    <#--<#if !attribute.isDerived()>-->
        <#if typeHelper.isOptional(attribute.getType())>
            <#assign default=typeHelper.getDefaultValue(attribute.getType().getName())>
        ${getterSetterHelper.getSetter(attribute)}(${originalName?uncap_first}.${getterSetterHelper.getGetter(attribute)}().orElse(${default}));
        <#else>
        ${getterSetterHelper.getSetter(attribute)}(${originalName?uncap_first}.${getterSetterHelper.getGetter(attribute)}());
        </#if>
    <#--</#if>-->
</#list>
<#list assocs as assoc>
    <#assign assocName=cdassoc.getAssociationName(assoc)?cap_first>
    <#if !assoc.isDerived()>
        <#if cdassoc.isOne(assoc)>
        ${assocName?uncap_first}Id = ${originalName?uncap_first}.get${assocName}().getId();
        <#elseif cdassoc.isMultiple(assoc)>
        ${assocName?uncap_first}Ids = new ArrayList<>();
            <#assign targetTypeName = assoc.getTargetType().getName()>
        for (${targetTypeName} o: ${originalName?uncap_first}.${assocNameUtil.getGetAllMethodName(assoc).orElse("")}()) {
        ${assocName?uncap_first}Ids.add(o.getId());
        }
        <#else>
        ${assocName?uncap_first}Id = get${assocName}Id(${originalName?uncap_first}.get${assocName}Optional());
        </#if>
    </#if>
</#list>}
