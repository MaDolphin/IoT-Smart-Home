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
  The init method in a unit test

  @author: SE RWTH Aachen
-->
${signature("identifier", "visibleFields", "assocs")}
<#assign cdassoc = tc.instantiate("common.util.CDAssociationUtil")>
<#assign typeHelper = tc.instantiate("common.util.TypeHelper")>
<#assign getterSetterHelper = tc.instantiate("common.util.GetterSetterHelper")>
<#assign assocUtil = tc.instantiate("common.util.CDAssociationUtil")>
{
  ${identifier}Builder builder = new ${identifier}Builder();
  ${identifier}DummyCreator creator = get${identifier}DummyCreator();
    // handle visible attributes
  <#list visibleFields as field>
    <#assign fieldType = field.getType()>
    <#if typeHelper.isOptional(fieldType)>
       builder.${field.getName()}(creator.get${field.getName()?cap_first}());
    <#else>
      builder.${field.getName()}(creator.${getterSetterHelper.getGetter(field)?uncap_first}());
    </#if>
</#list>
<#list assocs as assoc>
 <#assign targetType = assoc.getTargetType().getName()?cap_first>
 <#assign methodName = assoc.getDerivedName()?uncap_first>
 <#assign creatorClass = targetType + "DummyCreator">
 <#assign dummy = "dummy" + methodName?cap_first + "Creator">

 ${creatorClass} ${dummy} = get${dummy?cap_first}();
 <#if assocUtil.isMultiple(assoc) >
   builder.${methodName}(Lists.newArrayList(${dummy}.get${targetType}()));
 <#elseif !cdassoc.isOptional(assoc) >
   builder.${methodName}(${dummy}.get${targetType}());
 <#else>
   builder.${methodName}(Optional.of(${dummy}.get${targetType}()));
 </#if>
</#list>
 try {
    assertNotNull(builder.build());
 }
 catch (ValidationException e) {
    fail(e.getMessage());
 }
}
