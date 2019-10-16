<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attrName", "qualifierName")}
{
<#--  if (this.${attrName}.get(${qualifierName}) != null){-->
    return UnmodifiableListIterators.unmodifiableListIterator(this.${attrName}.get(${qualifierName}).listIterator(index));
<#--  } else {
    ${tc.include("common.templates.AssociationConstraintException", ast)}
  }-->
}
