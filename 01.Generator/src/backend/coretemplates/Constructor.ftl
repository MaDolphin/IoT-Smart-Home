<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates a Java constructor 

  @author: SE RWTH Aachen
-->
${tc.signature("ast")}

${defineHookPoint("<Block>?ClassConstructor:addAnnotations")}

${ast.printModifier()} ${ast.getName()}(${ast.printParametersDecl()})
  ${ast.printThrowsDecl()}

${tc.include("backend.coretemplates.EmptyMethod", ast)}
