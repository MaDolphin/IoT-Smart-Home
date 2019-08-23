<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  The core method to validate if an object is consistent.
  I.e. all OCL contrsints and all basic constraints will be checked.

  @author: SE RWTH Aachen
-->
${tc.signature("methodName", "getterMethods")}
{
  return ${methodName} (
    ${getterMethods}
  );
}
