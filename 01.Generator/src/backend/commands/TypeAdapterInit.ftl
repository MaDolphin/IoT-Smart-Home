<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cmdList")}
{
  this
  <#list cmdList as cmd>
    .registerSubtype(${cmd}.class)
  </#list>
  ;
}
