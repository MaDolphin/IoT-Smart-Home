<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attrName", "qualifierName")}
{
<#--  if (this.${attrName}.get(${qualifierName}) != null){-->
    return this.${attrName}.get(${qualifierName}).lastIndexOf(o);
<#--  } else {
    ${tc.include("common.templates.AssociationConstraintException", ast)}
  }-->
}
