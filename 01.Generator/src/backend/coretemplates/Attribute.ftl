<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates a Java attribute

-->
${tc.signature("ast")}
<#assign tansformUtil = tc.instantiate("common.util.TransformationUtils")>

${defineHookPoint("<Block>?ClassAttribute:addAnnotations")}
${tansformUtil.handleAttributeAnnotation(ast)}
${ast.printAnnotation()}
  ${ast.printModifier()} ${ast.printType()} ${ast.getName()} ${tc.include("backend.coretemplates.AttributeValue")}
