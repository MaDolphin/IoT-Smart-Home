<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates a Java method
  NOTE: This template exists because of a TODO in MC4.

-->
${tc.signature("ast")}
${ast.getName()}(${tc.includeArgs("frontend.coretemplates.ParameterList", [ast])}): ${ast.printReturnType()} ${ast.printThrowsDecl()};
