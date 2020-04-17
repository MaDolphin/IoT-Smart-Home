<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generate the initial value of a variable

-->

<#assign utils = tc.instantiate("frontend.common.FrontendTransformationUtils")>
<#if ast.isPresentValue()> = ${utils.getAttributeValue(ast.printValue())}; <#else> ; </#if>
