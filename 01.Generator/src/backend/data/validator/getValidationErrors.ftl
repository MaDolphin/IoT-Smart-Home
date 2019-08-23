<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  The core method to validate if an object is consistent.
  I.e. all OCL contrsints and all basic constraints will be checked.

  @author: SE RWTH Aachen
-->
${signature("identifier", "visibleFields", "checkAssocs", "hasSuperclass", "nonAttributeOclConstraints")}
<#assign param = identifier?uncap_first>

{
  StringBuilder msgs = new StringBuilder();
  msgs.append(getErrors(isNotNull(${param})));

<#if hasSuperclass>
   super.getValidationErrors(${param}).ifPresent(msgs::append);
</#if>

  // handle visible attributes
<#list visibleFields as field>
  <#if !field.isDerived()>
  msgs.append(getErrors(is${field.getName()?cap_first}Valid(${param})));
  </#if>
</#list>

  // handle associations
<#list checkAssocs as a>
  <#if !a.isDerived()>
  msgs.append(getErrors(is${a.getDerivedName()?cap_first}Valid(${param})));
  </#if>
</#list>

  // handle OCL constraints (non attribute related constraints)
<#list nonAttributeOclConstraints as method>
  msgs.append(getErrors(${method.getName()}(${param})));
</#list>

  if (msgs.length() != 0) {
    String header = ${param}.getHumanName() + " ist nicht korrekt:\n";
    Log.warn(header + msgs + "\n" + ${param}.toString());
    return Optional.of(header + msgs);
  }

  return Optional.empty();
}
