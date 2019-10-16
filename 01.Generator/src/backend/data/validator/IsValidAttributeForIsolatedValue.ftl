<#ftl encoding='UTF-8'>
<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  The core method in a builder to validate if an attribute value is consistent.
  
  It is independently usable of any containing object 
  (and thus usable by DTOs, domain classes, commands etc.)

  @author: SE RWTH Aachen
-->
${signature("field", "attributeRelatedOCLConstraints")}
<#assign typeHelper = tc.instantiate("common.util.TypeHelper")>
<#assign fieldName = field.getName()>
<#assign fieldType = field.getType()>
<#assign fieldTypeName = field.getType().getStringRepresentation()>

{
  // handle visible, non derived attribute only (otherwise method exists, but is empty)
<#if !field.isDerived()>
    <#if !typeHelper.isPrimitive(fieldType)>
  // primitives
  if (${fieldName} == null) {
    String msg = "Wert ${fieldName?cap_first} fehlt (0xD0200)\n";
    Log.debug("Validator: "+ msg, getClass().getName());
    return Optional.of(msg);
  }
    </#if>

    <#if typeHelper.isString(fieldType)>
  // String
  if (${fieldName}.isEmpty()) {
    String msg = "Text ${fieldName?cap_first} fehlt (0xD0300)\n";
    Log.debug(msg, getClass().getName());
    return Optional.of(msg);
  }
    </#if>

  Optional<String> oclMessage = Optional.empty();
    <#list attributeRelatedOCLConstraints as method>
  oclMessage = ${method.getName()}(${fieldName});
  if (oclMessage.isPresent()) {
    Log.debug("Validator: " + oclMessage.get(), getClass().getName());
    return oclMessage;
  }
    </#list>
</#if>

  return Optional.empty();
}
