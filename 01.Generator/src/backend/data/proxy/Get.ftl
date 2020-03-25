<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates the default method body for every get method of an attribute.

-->

${tc.signature("varName", "isOptional", "isList")}
{
  <#assign localName = "">
  <#if !ast.getModifier().isStatic()> 
    <#assign localName = "this.${varName}">
  <#else>
    <#assign localName = varName>
  </#if>
  <#if isOptional>
    return <#if !ast.getModifier().isStatic()> Optional.ofNullable(this.</#if>${varName});
  <#elseif isList>
    if (${localName} == null) {
      return new ArrayList<>();
    }
    return ${localName};
  <#else>
    return ${localName};
  </#if>
}
