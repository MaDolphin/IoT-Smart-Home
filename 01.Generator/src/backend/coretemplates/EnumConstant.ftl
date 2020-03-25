<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates a Java attribute

-->
${tc.signature("ast")}
<#assign util = tc.instantiate("common.util.TransformationUtils")>
  ${ast.getName()} ${util.printCDEnumParameters(ast.getCDEnumParameterList())};
