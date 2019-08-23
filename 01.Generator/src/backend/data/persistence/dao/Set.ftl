<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  @param CDTypeSymbol ast
-->
${tc.signature("className", "methodName", "attrName")}
{
  ${className} ${className?uncap_first} = em.getReference(${className}.class, id);
  ${className?uncap_first}.${methodName}(${attrName});
}
