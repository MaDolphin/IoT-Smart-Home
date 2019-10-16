<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("qualifierName", "attrName", "attrType", "newAttrName", "rawMethodCall", "isOneToMany")}

{
  if (this.${attrName}.containsKey(${qualifierName}) && this.${attrName}.get(${qualifierName}).contains(${newAttrName})){
    <#if isOneToMany>
      if (this.${attrName}.get(${qualifierName}).size() == 1){
        ${tc.includeArgs("backend.coretemplates.association.AssociationConstraintException", ["QUALIFIED.REMOVE.MULTIMAP",true])}
      }
    </#if>
    <#if rawMethodCall??>
      for (${attrType} el : this.${attrName}.get(${qualifierName})) {
        el.${rawMethodCall};
      }
    </#if>
    return this.${attrName}.get(${qualifierName}).remove(${newAttrName});
  }
  return false;
}
