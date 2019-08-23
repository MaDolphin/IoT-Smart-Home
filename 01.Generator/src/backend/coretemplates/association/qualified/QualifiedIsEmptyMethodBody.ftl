<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attrName", "qualifierName", "isOneToMany")}

{
<#--  if(this.${attrName}.get(${qualifierName}) != null){
    <#if isOneToMany>
      if (this.${attrName}.get(${qualifierName}).size() == 0){
        ${tc.include("common.templates.AssociationConstraintException", ast)}
      }
    </#if>-->
    return this.${attrName}.get(${qualifierName}).isEmpty();
<#--  } else {
    ${tc.include("common.templates.AssociationConstraintException", ast)}
  }-->
}
