<#-- avaiable in template: alert, account, budget -->
<#-- Condition -->
<#compress>
  <#if alert.repetitions lt 5 && alertHelper.dateDifference(alert.lastAlert, .now?date?string('yyyy-MM-dd')) gt 10>
  true
  </#if>
</#compress>
