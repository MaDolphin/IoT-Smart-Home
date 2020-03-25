<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  The core method to validate if an DTO-object is consistent.
  I.e. all OCL contraints and all basic constraints will be checked.
  
  (raw) DTO-objects do not contain links to other objects: 
  Thus assoc constraints are omitted here.

-->
${signature("identifier", "visibleFields", "hasSuperclass")}
<#assign param = identifier?uncap_first + "DTO">

{
  StringBuilder msgs = new StringBuilder();
  msgs.append(getErrors(isNotNullDTO(${param})));

<#if hasSuperclass>
  super.getValidationErrors(${param}).ifPresent(msgs::append);
</#if>

  // handle visible attributes
<#list visibleFields as field>
  <#if !field.isDerived()>
  msgs.append(getErrors(is${field.getName()?cap_first}ValidDTO(${param})));
  </#if>
</#list>

  if (msgs.length() != 0) {
    String header = "DTO-Objekt ${identifier} ist nicht korrekt:\n";
    Log.warn(header + msgs + "\n" + ${param}.toString());
    return Optional.of(header + msgs);
  }

  return Optional.empty();
}
