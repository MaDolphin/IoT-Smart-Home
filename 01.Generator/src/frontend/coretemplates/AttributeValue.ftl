<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generate the initial value of a variable

  @author: SE RWTH Aachen
-->
<#if ast.valueIsPresent()> = ${ast.printValue()}; <#else> ; </#if>
