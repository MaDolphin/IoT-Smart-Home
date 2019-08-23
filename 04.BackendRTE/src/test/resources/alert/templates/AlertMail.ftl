<#-- (c) https://github.com/MontiCore/monticore -->
<#-- avaiable in template: alert, account, budget, user -->
Subject: Erinnerung: ${account.name} überzogen<br>
To: ${user.email}<br>
<br>
<#if alert.repetitions gt 0 >
Erinnerung!<br>
<#else>
=== Neuer Alert ===<br>
</#if>
<br>
Sehr geehrter Nutzer ${user.username},<br>
<br>
das  Konto ${account.name} <br>
PSP  ${account.getPspElement().get()} <br>
vom  Typ ${account.getKontotyp().get()}  >  ${account.getKontotyp().get()} <br>
mit  Budgetrahmen ${budget.getBudgetRahmenCent().get()} € <br>
<br>
ist aktuell um <br>
${budget.getBudgetRahmenCent().get()} € <br>
überzogen. <br>
<br>
Ihre Optionen:<br>
1) Umbuchungen vornehmen<br>
2) Erlaubten Überziehungwert anpassen<br>
3) In den MaCoCo Einstellungen den Alert abschalten<br>
<br>
<#compress>
  <#if alert.repetitions gt 0 >
Dies ist die ${alert.repetitions + 1} te Wiederholung dieser Email. Der Alert besteht seit ${alertHelper.formatDate(alert.firstAlert)}.<br>
  <#else> Dies ist die erste Email zu diesem Alert.<br>
    ${alert.setRepetitions(0)}<br>
    ${alert.setRepetitions(alert.repetitions + 1)}<br>
    ${alert.setFirstAlert(alertHelper.toZonedDateTime(.now?iso_utc))}<br>
  </#if>
  ${alert.setLastAlert(alertHelper.toZonedDateTime(.now?iso_utc))}<br>
  (Diese Email wird wöchentlich wiederholt.)<br>
</#compress>
<br>
Ihr MaCoCo System
