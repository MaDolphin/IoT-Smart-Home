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


${tc.signature("symTab", "dtoClass", "domainClass", "attributes")}
<#assign typeHelper = tc.instantiate("common.util.TypeHelper")>
<#assign getterSetterHelper = tc.instantiate("common.util.GetterSetterHelper")>
<#assign astPrinter = tc.instantiate("de.monticore.umlcd4a.prettyprint.AstPrinter")>
<#assign utils = tc.instantiate("common.util.TransformationUtils")>
<#assign identifier = domainClass>
{
${identifier}Builder builder = new ${identifier}Builder();
builder.id(this.getId());

<#list attributes as attr>
    <#assign attrType = attr.getType()>
    <#assign attrName = attr.getName()>

    <#if typeHelper.isOptional(attr.getType())>
            if (${attrName} != null) {
                builder.${attrName}(Optional.of(${attrName}));
            }
            else{
                builder.${attrName}(Optional.empty());
            }
    <#elseif utils.isDTOType(symTab, attr.getType())>
            builder.${attrName}(this.${getterSetterHelper.getGetter(attr)}().toBuilder().build());
    <#else>
            builder.${attrName}(this.${getterSetterHelper.getGetter(attr)}());
    </#if>
</#list>

    // return the builder for further processing
    return builder;
}