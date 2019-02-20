<#ftl encoding='UTF-8'>
<#--
****************************************************************************
MontiCore Language Workbench, www.monticore.de
Copyright (c) 2017, MontiCore, All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its
contributors may be used to endorse or promote products derived from this
software without specific prior written permission.

This software is provided by the copyright holders and contributors
"as is" and any express or implied warranties, including, but not limited
to, the implied warranties of merchantability and fitness for a particular
purpose are disclaimed. In no event shall the copyright holder or
contributors be liable for any direct, indirect, incidental, special,
exemplary, or consequential damages (including, but not limited to,
procurement of substitute goods or services; loss of use, data, or
profits; or business interruption) however caused and on any theory of
liability, whether in contract, strict liability, or tort (including
negligence or otherwise) arising in any way out of the use of this
software, even if advised of the possibility of such damage.
****************************************************************************
-->
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
