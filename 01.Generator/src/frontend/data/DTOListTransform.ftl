<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("hasSuperclass")}
{
<#if hasSuperclass>
  super.transform();
</#if>

this.dto.forEach(d => d.transform());
}
