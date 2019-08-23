<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className", "originalName", "assocs")}
<#assign getterSetterHelper = tc.instantiate("common.util.GetterSetterHelper")>
<#assign nameHelper = tc.instantiate("backend.coretemplates.association.AssociationNameUtil")>
<#assign assocHelper = tc.instantiate("common.util.CDAssociationUtil")>
<#assign objName=originalName?uncap_first>
{
  super(${objName});
  this.typeName = "${className}";

<#list assocs as assoc>
  <#assign assocName=assocHelper.getAssociationName(assoc)>
  <#if assocHelper.isOptional(assoc)>
    ${objName}.get${assocName?cap_first}Optional().ifPresent(a->${assocName}=a.getId());
  <#elseif assocHelper.isMultiple(assoc)>
    this.${assocName}List = Lists.newArrayList();
    ${objName}.${nameHelper.getGetAllMethodName(assoc).orElse("")}().stream().forEach(a -> ${assocName}List.add(a.getId()));
  <#else>
    this.${assocName} = ${objName}.get${assocName?cap_first}().getId();
  </#if>
</#list>

}
