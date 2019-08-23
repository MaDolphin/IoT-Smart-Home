<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  The core method to validate if an object is consistent.
  I.e. all OCL contrsints and all basic constraints will be checked.

  @author: SE RWTH Aachen
-->
${tc.signature("constraints", "message")}
{
  boolean constraintFailed = false;
  <#list constraints as c>
  ${c}
  </#list>

  if (constraintFailed) {
    return Optional.of(${message} + "\n");
  }
  return Optional.empty();
}
