<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates Java-enums from CDEnums including an extension point for each 
  content of an enum (enum-constants, attributes, constructors, methods, 
  and associations).
  
  @author: SE RWTH Aachen
-->
${tc.signature("package", "importList")}
${defineHookPoint("EnumContent:addComment")}
<#assign util = tc.instantiate("frontend.common.FrontendTransformationUtils")>

<#list importList as i>
  import ${i};
</#list>

export enum ${ast.getName()}
{
  <#assign delim="">
  <#list ast.getCDEnumConstantList() as enumConst>
    ${delim} ${enumConst.getName()}${util.printCDEnumParameters(enumConst.getCDEnumParameterList())}
    <#assign delim=",">
  </#list>
}

  <#-- generate all methods -->
  <#list ast.getCDMethodList() as method>
    ${tc.includeArgs("frontend.coretemplates.EnumMethod", [method])}
  </#list>

