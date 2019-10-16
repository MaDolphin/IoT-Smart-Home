<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("childClass", "dtos", "opts", "lists")}
{
   replaceMinusOnes(this);

<#if childClass>
    super.transform();
</#if>
<#list dtos as dto>
   this.${dto.getName()}.transform();
</#list>
<#list opts as opt>
   if(this.${opt.getName()} != null) {this.${opt.getName()}.transform()};
</#list>
<#list lists as list>
   this.${list.getName()}.forEach(dto => dto.transform());
</#list>
}
