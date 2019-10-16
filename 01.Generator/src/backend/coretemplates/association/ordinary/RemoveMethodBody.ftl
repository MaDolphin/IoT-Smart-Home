<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attrName", "newAttrName", "rawMethodCall", "isOneToMany", "methodName", "isComposition")}

{
  if (this.${attrName}.contains(${newAttrName})){
    <#if isComposition>
      <#assign h = tc.instantiate("common.util.TransformationUtils")>
      throw new DataConsistencyException("${h.getErrorCode()}: ${methodName}: Association links cannot be removed from compositions.");
    <#else>
      <#if isOneToMany>
      if (this.${attrName}.size() <= 1){
        ${tc.includeArgs("backend.coretemplates.association.AssociationConstraintException", [methodName,false])}
      }
      </#if>
    
      <#if rawMethodCall??>
        ${newAttrName}.${rawMethodCall};
      </#if>
    
      return this.${attrName}.remove(${newAttrName});
    </#if>
  }
  return false;
}
