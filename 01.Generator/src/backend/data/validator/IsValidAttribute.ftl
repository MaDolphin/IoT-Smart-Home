<#ftl encoding='UTF-8'>
<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  The core method in a builder to validate if an object is consistent.

-->
${signature("field", "identifier")}
<#assign typeHelper = tc.instantiate("common.util.TypeHelper")>
<#assign getterSetterHelper = tc.instantiate("common.util.GetterSetterHelper")>
<#assign fieldName = field.getName()>
<#assign fieldType = field.getType()>
<#assign fieldTypeName = field.getType().getStringRepresentation()>

{
  // delegate to implementing method (which is a hook for overwriting)
  return is${fieldName?cap_first}ValidValue(
       ${identifier}.${getterSetterHelper.getGetter(field)?uncap_first}());

}
