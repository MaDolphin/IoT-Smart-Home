<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generate the initial value of a variable

-->
<#if ast.isPresentValue()> = ${ast.printValue()}; <#else> ; </#if>
