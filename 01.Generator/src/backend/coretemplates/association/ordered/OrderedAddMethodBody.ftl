<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attrName", "newAttrName", "rawMethodCall")}

{
  if (!this.${attrName}.contains(${newAttrName})){
    <#-- If this is a bidirectional association, then consistency needs to 
         be managed. -->
    <#if rawMethodCall??>
      o.${rawMethodCall};
    </#if>

    this.${attrName}.add(index, o);
  }
}
