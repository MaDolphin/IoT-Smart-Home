<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates a Java method

-->

${tc.signature("method")}

${defineHookPoint("<Block>?ClassMethod:addAnnotations")}

${method.printAnnotation()}
export function ${method.getName()}(${tc.includeArgs("frontend.coretemplates.ParameterList", [method])}): ${method.printReturnType()} ${method.printThrowsDecl()}
${tc.include("backend.coretemplates.EmptyMethod", method)}
