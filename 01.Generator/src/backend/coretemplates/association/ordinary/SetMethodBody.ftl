<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attrName", "newAttrName", "rawMethodCall", "containsMethodCall", "interfaceName", "isMultiply")}

{
 this.${attrName} = ${newAttrName};
 <#if rawMethodCall??>
   <#if isMultiply && containsMethodCall?has_content>
   if(!this.${attrName}.${containsMethodCall}((${interfaceName}) this)) {
     this.${attrName}.${rawMethodCall};
   }
   <#else>
   this.${attrName}.${rawMethodCall};
   </#if>
 </#if>
}
