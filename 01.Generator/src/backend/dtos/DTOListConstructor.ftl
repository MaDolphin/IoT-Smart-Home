<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className", "superArguments", "attributes")}
<#assign getterSetterHelper = tc.instantiate("common.util.GetterSetterHelper")>
{
super( ${superArguments?join(",")} );
this.typeName = "${className}";
<#if superArguments?size == 0>
  this.setId(id);
</#if>
this.setDtos(dtos);
}
