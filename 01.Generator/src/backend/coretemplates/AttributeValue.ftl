<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generate the initial value of a variable

  @author: SE RWTH Aachen
-->
<#if ast.isPresentValue()> = ${ast.printValue()}; <#else> ; </#if>
