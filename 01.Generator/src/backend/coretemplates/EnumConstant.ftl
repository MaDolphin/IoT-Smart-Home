<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates a Java attribute

  @author: SE RWTH Aachen
-->
${tc.signature("ast")}
<#assign util = tc.instantiate("common.util.TransformationUtils")>
  ${ast.getName()} ${util.printCDEnumParameters(ast.getCDEnumParameterList())};
