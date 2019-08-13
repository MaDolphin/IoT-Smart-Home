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


${tc.signature("proxyClass", "attributes")}
<#assign typeHelper = tc.instantiate("common.util.TypeHelper")>
<#assign getterSetterHelper = tc.instantiate("common.util.GetterSetterHelper")>
<#assign astPrinter = tc.instantiate("de.monticore.umlcd4a.prettyprint.AstPrinter")>
<#assign identifier = proxyClass?keep_before("Proxy")>
{
    ${identifier}Builder builder = new ${identifier}Builder();

    <#list attributes as attr>
        <#assign attrType = attr.getType()>
        <#assign attrName = attr.getName()>
        <#assign attrNameSuffix = getterSetterHelper.getNameSuffix(attr)>

        <#if typeHelper.isOptional(attr.getType())>
            if (${getterSetterHelper.getGetNamePrefix(attr)}${attrName?cap_first}() != null) {
                builder.${attrName}(Optional.of(${getterSetterHelper.getGetNamePrefix(attr)}${getterSetterHelper.getNameSuffix(attr)?cap_first}()));
            }
            else{
                builder.${attrName}(Optional.empty());
            }
        <#else>
            builder.${attrName}(${getterSetterHelper.getGetNamePrefix(attr)}${getterSetterHelper.getNameSuffix(attr)?cap_first}());
        </#if>
    </#list>

    <#--builder.budget(setBudgetIfPresent(bdao));
    builder.externalAccount(setExternalAccountIfPresent(adao));-->
    /* Build Data Object */
    ${identifier?cap_first} ${identifier?uncap_first} = builder.build();
    Log.debug("getDataClass: " + ${identifier?uncap_first}, getClass().getName());

    return ${identifier?uncap_first};
}
