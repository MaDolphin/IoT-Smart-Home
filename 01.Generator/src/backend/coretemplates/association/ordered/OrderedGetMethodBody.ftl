<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("varName", "lowerBoundCheck")}

{
<#--  <#if lowerBoundCheck == true>
    if (this.${varName} == null){
      ${tc.include("common.templates.AssociationConstraintException", ast)}
    }
  </#if>-->
  return this.${varName}.get(index);
}
