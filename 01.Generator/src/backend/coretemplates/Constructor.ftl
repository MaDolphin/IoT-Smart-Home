<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates a Java constructor 

-->
${tc.signature("ast")}

${defineHookPoint("<Block>?ClassConstructor:addAnnotations")}

${ast.printModifier()} ${ast.getName()}(${ast.printParametersDecl()})
  ${ast.printThrowsDecl()}

${tc.include("backend.coretemplates.EmptyMethod", ast)}
