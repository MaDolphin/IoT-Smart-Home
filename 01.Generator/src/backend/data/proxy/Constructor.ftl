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
${tc.signature("originalName", "attributes", "assocs")}
<#assign getterSetterHelper = tc.instantiate("common.util.GetterSetterHelper")>
<#assign cdassoc = tc.instantiate("common.util.CDAssociationUtil")>
<#assign typeHelper = tc.instantiate("common.util.TypeHelper")>
<#assign assocNameUtil = tc.instantiate("backend.coretemplates.association.AssociationNameUtil")>

{
id = ${originalName?uncap_first}.getId();
<#list attributes as attribute>
    <#--<#if !attribute.isDerived()>-->
        <#if typeHelper.isOptional(attribute.getType())>
            <#assign default=typeHelper.getDefaultValue(attribute.getType().getName())>
        ${getterSetterHelper.getSetter(attribute)}(${originalName?uncap_first}.${getterSetterHelper.getGetter(attribute)}().orElse(${default}));
        <#else>
        ${getterSetterHelper.getSetter(attribute)}(${originalName?uncap_first}.${getterSetterHelper.getGetter(attribute)}());
        </#if>
    <#--</#if>-->
</#list>
<#list assocs as assoc>
    <#assign assocName=cdassoc.getAssociationName(assoc)?cap_first>
    <#if !assoc.isDerived()>
        <#if cdassoc.isOne(assoc)>
        ${assocName?uncap_first}Id = ${originalName?uncap_first}.get${assocName}().getId();
        <#elseif cdassoc.isMultiple(assoc)>
        ${assocName?uncap_first}Ids = new ArrayList<>();
            <#assign targetTypeName = assoc.getTargetType().getName()>
        for (${targetTypeName} o: ${originalName?uncap_first}.${assocNameUtil.getGetAllMethodName(assoc).orElse("")}()) {
        ${assocName?uncap_first}Ids.add(o.getId());
        }
        <#else>
        ${assocName?uncap_first}Id = get${assocName}Id(${originalName?uncap_first}.get${assocName}Optional());
        </#if>
    </#if>
</#list>}
