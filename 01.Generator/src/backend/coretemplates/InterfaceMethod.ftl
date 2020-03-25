<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates a Java method
  NOTE: This template exists because of a TODO in MC4.

-->
${tc.signature("ast")}

${defineHookPoint("<Block>?InterfaceMethod:addAnnotations")}

${ast.printModifier()}  ${ast.printReturnType()} ${ast.getName()} ( ${ast.printParametersDecl()} )
  ${ast.printThrowsDecl()} ;
