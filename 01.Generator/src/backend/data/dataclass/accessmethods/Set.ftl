<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates the default set method for every attribute.

  @author: SE RWTH Aachen
-->

${tc.signature("attrType", "attrName", "newAttrName", "isOptional")}
<#assign typeHelper = tc.instantiate("common.util.TypeHelper")>
{
  <#-- Check if there is exactly one parameter -->
  <#assign localAttrName = attrName>
  <#if !ast.getModifier().isStatic()> 
    <#assign localAttrName = "this."+attrName>
  </#if>

  <#-- for every non-primitive attribute check that it is not null -->
  <#if !typeHelper.isPrimitive(attrType)>
    ${defineHookPoint("<Block>*Set:addCheck")}
  </#if>

  <#if isOptional>
    ${localAttrName} = ${newAttrName}.orElse(null);
  <#else>
    ${localAttrName} = ${newAttrName};
  </#if>
}
