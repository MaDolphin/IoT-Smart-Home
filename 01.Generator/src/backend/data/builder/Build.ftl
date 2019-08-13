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
  The build method is generated to allow object creation.

  @author: SE RWTH Aachen
-->
${tc.signature("class", "paramList", "allAssociations")}

<#assign assocUtil = tc.instantiate("common.util.CDAssociationUtil")>
<#assign transformationUtils = tc.instantiate("common.util.TransformationUtils")>
<#assign typeHelper = tc.instantiate("common.util.TypeHelper")>
{
<#if class.isAbstract()>
throw new org.apache.commons.lang.NotImplementedException("${transformationUtils.getErrorCode()}: Cannot create ${class.name} object, because it is abstract.");
<#else>
    <#assign constructorArguments = []>

    <#list paramList as attr>
        <#assign constructorArguments = constructorArguments + ["${attr.getName()}"]>
    </#list>

// Create the actual object
${class.getName()} newObj = new ${class.getName()}().rawInitAttrs( ${constructorArguments?join(",")} );

// Set the id
newObj.setId(id);

if (addToBidirectionalAssociation) {
<#list allAssociations as assoc>
    <#if assoc.isBidirectional()>
        <#if assocUtil.isSourceOne(assoc) || assocUtil.isSourceOptional(assoc)>
            <#if assocUtil.isTargetOne(assoc)>
                ${assocUtil.associationToCDParameter(assoc).getName()}.rawSet${(assocUtil.getOppositeAssociationName(assoc))?cap_first}(newObj);
            <#elseif assocUtil.isTargetOptional(assoc)>
                ${assocUtil.associationToCDParameter(assoc).getName()}.ifPresent(o -> o.rawSet${(assocUtil.getOppositeAssociationName(assoc))?cap_first}(newObj));
            <#elseif assocUtil.isTargetMultiple(assoc)>
                for (${typeHelper.getFirstTypeArgumentOfGeneric(assocUtil.getAssociationType(assoc))} o : ${assocUtil.getAssociationName(assoc)}) {
                    o.rawSet${(assocUtil.getOppositeAssociationName(assoc))?cap_first}(newObj);
                }
            </#if>
        <#elseif assocUtil.isSourceMultiple(assoc)>
            <#if assocUtil.isTargetOne(assoc)>
                ${assocUtil.associationToCDParameter(assoc).getName()}.add${(assocUtil.getOppositeAssociationName(assoc))?cap_first}(newObj);
            <#elseif assocUtil.isTargetOptional(assoc)>
                ${assocUtil.associationToCDParameter(assoc).getName()}.ifPresent(o -> o.add${(assocUtil.getOppositeAssociationName(assoc))?cap_first}(newObj));
            <#elseif assocUtil.isTargetMultiple(assoc)>
            for (${typeHelper.getFirstTypeArgumentOfGeneric(assocUtil.getAssociationType(assoc))} o : ${assocUtil.getAssociationName(assoc)}) {
                o.add${(assocUtil.getOppositeAssociationName(assoc))?cap_first}(newObj);
            }
            </#if>
        </#if>
    </#if>
</#list>
}

Optional<String> validationMessages = ${class.getName()}Validator.getValidator().getValidationErrors(newObj);
if (validationMessages.isPresent()) {
  throw new ValidationException(validationMessages.get());
}
return newObj;
</#if>
}
