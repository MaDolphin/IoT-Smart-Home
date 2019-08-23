<#-- (c) https://github.com/MontiCore/monticore -->
<#ftl encoding='UTF-8'>
<#--
  The core method in a builder to validate if an object is consistent.

  @author: SE RWTH Aachen
-->
${signature("symTab", "field", "identifier")}
<#assign typeHelper = tc.instantiate("common.util.TypeHelper")>
<#assign getterSetterHelper = tc.instantiate("common.util.GetterSetterHelper")>
<#assign utils = tc.instantiate("common.util.TransformationUtils")>
<#assign fieldName = field.getName()>
<#assign fieldType = field.getType()>
<#assign fieldTypeName = field.getType().getStringRepresentation()>

{
  // delegate to implementing method (which is a hook for overwriting)

<#assign isOptional = typeHelper.isGenericOptional(fieldTypeName)>

<#if isOptional>
    <#assign optEncode = "Optional.ofNullable">
<#else>
    <#assign optEncode = "">
</#if>

<#if utils.isDTOType(symTab, fieldType)>
  try {
    ${fieldTypeName} v2 = ${optEncode}(${identifier}.${getterSetterHelper.getGetter(field)?uncap_first}().toBuilder().build());
    return is${fieldName?cap_first}ValidValue(v2);
  } catch(ValidationException e) {
    return Optional.of(e.getMessage());
  }
<#else>
  ${fieldTypeName} v2 = ${optEncode}(${identifier}.${getterSetterHelper.getGetter(field)?uncap_first}());
  return is${fieldName?cap_first}ValidValue(v2);
</#if>
}
