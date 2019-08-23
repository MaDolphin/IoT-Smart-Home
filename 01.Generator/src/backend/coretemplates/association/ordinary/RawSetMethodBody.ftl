<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attrName", "newAttrName", "removeMethodCall", "oppositeQualified")}

{

  <#--<#if removeMethodCall?? && !oppositeQualified>
  if(this.${attrName} != null){
    this.${attrName}.${removeMethodCall};
  }
  </#if>-->

  this.${attrName} = ${newAttrName} ;
}
