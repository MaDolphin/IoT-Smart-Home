<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attrName", "newAttrName", "rawMethodCall", "containsMethodCall", "interfaceName", "isMultiply")}

{

  <#if rawMethodCall??>
    this.${attrName} = o.orElse(null);
    if (null != this.${attrName} <#if isMultiply && containsMethodCall?has_content>&& !this.${attrName}.${containsMethodCall}((${interfaceName}) this)</#if>) {
      this.${attrName}.${rawMethodCall};
    }
  <#else>
    this.${attrName}  = ${newAttrName}.orElse(null);
  </#if>
}
