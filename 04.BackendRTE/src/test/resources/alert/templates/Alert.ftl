<#-- (c) https://github.com/MontiCore/monticore -->
<#-- avaiable in template: alert, account, budget -->
<#-- Condition -->
<#compress>
  <#if alert.repetitions lt 5 && budget.getBudgetRahmenCent().get() lt 1000000 >
  true
  </#if>
</#compress>
