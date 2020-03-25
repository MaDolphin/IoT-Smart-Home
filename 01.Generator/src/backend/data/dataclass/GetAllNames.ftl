<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates the getAllNames for enums.

-->
${tc.signature("enum")}
<#assign utils = tc.instantiate("common.util.TransformationUtils")>
{
<#assign delim = "">
return Lists.newArrayList(
<#list enum.getCDEnumConstantList() as enumConst>
  <#assign paramList=enumConst.getCDEnumParameterList()>
  <#list paramList as param>
    ${delim}${utils.printCDEnumParameter(param)}
  </#list>
  <#assign delim = ", ">
</#list>
);
}
