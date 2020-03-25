<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates the default method body for every get method of an attribute.

-->

${tc.signature("varName", "isOptional")}
{
  <#assign localName = "">
  <#if !ast.getModifier().isStatic()> 
    <#assign localName = "this.${varName}">
  <#else>
    <#assign localName = varName>
  </#if>
  <#if isOptional>
    return <#if !ast.getModifier().isStatic()> Optional.ofNullable(this.</#if>${varName});
  <#else>
    return <#if !ast.getModifier().isStatic()> this.</#if>${varName};
  </#if>
}
