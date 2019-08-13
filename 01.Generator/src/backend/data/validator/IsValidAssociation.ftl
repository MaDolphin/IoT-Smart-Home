<#-- (c) https://github.com/MontiCore/monticore -->
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
  The core method in a builder to validate if an object is consistent.

  @author: SE RWTH Aachen
-->
${signature("className", "assoc", "type", "oclConstraints")}
<#assign cdassoc = tc.instantiate("common.util.CDAssociationUtil")>
<#assign assocName = tc.instantiate("backend.coretemplates.association.AssociationNameUtil")>
<#assign attr = assoc.getTargetType()?uncap_first>

{
  // handle associations
  <#if cdassoc.isOptional(assoc) >
    ${type} ${attr} = value.get${assoc.getDerivedName()?cap_first}Optional();
  <#elseif cdassoc.isMultiple(assoc) >
    ${type} ${attr} = value.${assocName.getGetAllMethodName(assoc).orElse("")}();
  <#else>
    ${type} ${attr} = value.get${assoc.getDerivedName()?cap_first}();
  </#if>

  if (${attr} == null){
    String msg = "Assoziation " + ${className}.getHumanNameForAssociation${assoc.getDerivedName()?cap_first}() + " muss gesetzt werden.\n";
    Log.debug(msg, getClass().getName());
    return Optional.of(msg);
  }

  // ocl constraints
  Optional<String> oclMessage = Optional.empty();
    <#list oclConstraints as method>
  oclMessage = ${method.getName()}(${attr});
  if (oclMessage.isPresent()) {
    Log.debug("Validator: " + oclMessage.get(), getClass().getName());
    return oclMessage;
  }
    </#list>

  <#if cdassoc.isOptional(assoc) >
  if (${attr}.isPresent()) {
    return ${attr}.get().getValidator().getValidationErrors(${attr}.get());
  }
  return Optional.empty();
  
  <#elseif cdassoc.isMultiple(assoc) >
  if (${attr}.size() > 0) {
    StringBuilder msgs = new StringBuilder();
    ${attr}.stream().map(o -> o.getValidator().getValidationErrors(o)).forEach(o -> o.ifPresent(msgs::append));
    if (msgs.length() != 0) {
      return Optional.of(msgs.toString());
    }
  }
  return Optional.empty();
  
  <#else>
  return ${attr}.getValidator().getValidationErrors(${attr});
  </#if>
}
