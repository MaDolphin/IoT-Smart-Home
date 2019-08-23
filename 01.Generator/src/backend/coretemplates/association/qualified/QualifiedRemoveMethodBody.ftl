<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("qualifierName", "attrName", "rawMethodCall")}

{
  if (this.${attrName}.containsKey(key)){
    <#if rawMethodCall??>
      this.${attrName}.get(key).${rawMethodCall};
    </#if>
    this.${attrName}.remove(key);
    return true;
  }
  return false;
}
