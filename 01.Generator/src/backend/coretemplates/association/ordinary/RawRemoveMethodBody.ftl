<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attrName", "newAttrName", "isOneToMany", "methodName", "isComposition")}

{
  <#if isComposition>
    <#assign h = tc.instantiate("common.util.TransformationUtils")>
    throw new DataConsistencyException("${h.getErrorCode()}: ${methodName}: Association links cannot be removed from compositions.");
  <#else>
    <#if isOneToMany>
      if (this.${attrName}.contains(${newAttrName}) && this.${attrName}.size() <= 1){
        ${tc.includeArgs("backend.coretemplates.association.AssociationConstraintException", [methodName, false])}
      }
    </#if>
    return this.${attrName}.remove(${newAttrName});
  </#if>
}
