<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("className", "domainClass", "superArguments", "attributes")}
<#assign getterSetterHelper = tc.instantiate("common.util.GetterSetterHelper")>
{
super( ${superArguments?join(",")} );
this.typeName = "${className}";
<#if superArguments?size == 0>
  this.setId(id);
</#if>
this.setDtos(dtos.stream().map(d -> new ${domainClass}(d)).collect(Collectors.toList()));
}

