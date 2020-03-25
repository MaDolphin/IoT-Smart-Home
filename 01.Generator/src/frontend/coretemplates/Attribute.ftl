<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates a Java attribute

-->
${tc.signature("ast")}
${ast.printAnnotation()}
  ${ast.printModifier()} ${ast.getName()}: ${ast.printType()} ${tc.include("backend.coretemplates.AttributeValue")}
