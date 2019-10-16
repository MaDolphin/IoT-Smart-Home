<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("qualifierName", "attrType", "attrName", "rawMethodCall")}

{
  if (this.${attrName}.containsKey(key)) {
    <#if rawMethodCall??>
      for ( ${attrType} el: this.${attrName}.get(key)){
        el.${rawMethodCall};
      }
    </#if>
    this.${attrName}.removeAll(key);
    return true;
  }
  return false;
}
