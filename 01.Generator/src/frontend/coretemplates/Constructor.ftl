<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates a Java constructor 

-->
${tc.signature("constructor")}

${defineHookPoint("<Block>?ClassConstructor:addAnnotations")}

${constructor.printModifier()} constructor(${tc.includeArgs("frontend.coretemplates.ParameterList", [constructor])})
  ${constructor.printThrowsDecl()}

${tc.include("backend.coretemplates.EmptyMethod", constructor)}
