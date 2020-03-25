<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates the getAllNames for enums.

-->
${tc.signature("enum")}
<#assign utils = tc.instantiate("common.util.TransformationUtils")>
{
<#assign delim = "">
return [
<#list enum.getCDEnumConstantList() as enumConst>
  <#assign paramList=enumConst.getCDEnumParameterList()>
  ${delim}{key: "${enumConst.getName()}", value:
  <#list paramList as param>
    ${utils.printCDEnumParameter(param)}
  </#list>
  }
  <#assign delim = ", ">
</#list>
];

}
