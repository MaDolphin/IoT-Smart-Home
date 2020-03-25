<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates a Java method

-->

${tc.signature("ast")}
<#assign tansformUtil = tc.instantiate("common.util.TransformationUtils")>
${defineHookPoint("<Block>?ClassMethod:addAnnotations")}

${tansformUtil.handleMethodAnnotation(ast)}
${ast.printAnnotation()}
${ast.printModifier()} ${ast.printReturnType()} ${ast.getName()}(${ast.printParametersDecl()}) ${ast.printThrowsDecl()}
${tc.include("backend.coretemplates.EmptyMethod", ast)}
