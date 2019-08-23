<#-- (c) https://github.com/MontiCore/monticore -->
<#ftl encoding='UTF-8'>
<#--
  The core method in a builder to validate if an object is consistent.

  @author: SE RWTH Aachen
-->
${signature("validatorClassName", "field", "oclConstraints")}
<#assign typeHelper = tc.instantiate("common.util.TypeHelper")>
<#assign getterSetterHelper = tc.instantiate("common.util.GetterSetterHelper")>
<#assign fieldName = field.getName()>
<#assign fieldType = field.getType()>
<#assign fieldTypeName = field.getType().getStringRepresentation()>

{
    <#if !typeHelper.isOptional(fieldType)>
    if (val === null) {
        throw new ValidationError("${fieldName?cap_first} fehlt\n");
    }
    </#if>

    <#if  typeHelper.isString(fieldType) || typeHelper.isOptionalString(fieldType)>
    if (val !== null && val.length === 0) {
        throw new ValidationError("${fieldName?cap_first} fehlt\n");
    }
    </#if>

    <#if typeHelper.isZonedDateTime(fieldType) || typeHelper.isOptionalZonedDateTime(fieldType)>
    if (val !== null && typeof val === 'string' && (<string> val).length === 0) {
        throw new ValidationError("${fieldName?cap_first} fehlt\n");
    }
    </#if>

    <#list oclConstraints as constr>
    ${validatorClassName}.${constr.getName()}(val);
    </#list>
}
