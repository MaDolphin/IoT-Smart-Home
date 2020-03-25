<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates the default method body for every get method of an attribute.

-->

${tc.signature("varName")}
{
  <#if !ast.getModifier().isStatic()> 
    <#assign localName = "this.${varName}">
  <#else>
    <#assign localName = varName>
  </#if>
  return ${localName};
}
