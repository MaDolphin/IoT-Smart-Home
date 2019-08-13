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
  The init method in a unit test

  @author: SE RWTH Aachen
-->
${signature("identifier","visibleFields", "oneAssocs", "fieldsOfAssocs")}
{
try {
    <#assign firstAssoc = "">
    <#assign previousAssoc = "">
    <#list oneAssocs?reverse as oneAssoc>
    ${oneAssoc}Builder builder${oneAssoc} = new ${oneAssoc}Builder();
        <#if previousAssoc != "">
    builder${oneAssoc}.${previousAssoc?uncap_first}(tmp${previousAssoc});
        </#if>
    ${oneAssoc}DummyCreator creator${oneAssoc} = get${oneAssoc}DummyCreator();

        <#if firstAssoc = ""><#assign firstAssoc = oneAssoc></#if>
        <#list fieldsOfAssocs as k>
            <#if k[0] == "#">
            <#assign class = k[1..]>
                <#else>
                <#if class == oneAssoc>
                    <#if k[0..2] == "get">
    builder${oneAssoc}.${k[3..]?uncap_first}(creator${oneAssoc}.${k}());
                    <#elseif k[0..1] == "is">
                      <#if k[2..4] == "Has">
    builder${oneAssoc}.has${k[5..]}(creator${oneAssoc}.${k}());
                      <#else>
    builder${oneAssoc}.is${k[2..]?cap_first}(creator${oneAssoc}.${k}());
                      </#if>
                    </#if>
                </#if>
            </#if>
        </#list>
    ${oneAssoc} tmp${oneAssoc} = builder${oneAssoc}.build();

    tmp${oneAssoc} = ${oneAssoc?uncap_first}DWO.create(tmp${oneAssoc});
    assertNotNull(tmp${oneAssoc});
        <#assign previousAssoc = oneAssoc>
    </#list>

  <#assign typeHelper = tc.instantiate("common.util.TypeHelper")>
  <#assign getterSetterHelper = tc.instantiate("common.util.GetterSetterHelper")>
  // Builder for current class
  ${identifier}Builder builder = new ${identifier}Builder();
  ${identifier}DummyCreator creator = get${identifier}DummyCreator();
  // handle visible attributes of current class
<#list visibleFields as field>
  <#assign fieldType = field.getType()>
  <#if typeHelper.isOptional(fieldType)>
  builder.${field.getName()}(creator.get${field.getName()?cap_first}());
  <#else>
  builder.${field.getName()}(creator.${getterSetterHelper.getGetter(field)?uncap_first}());
  </#if>
</#list>

  <#if previousAssoc != "">
    builder.${previousAssoc?uncap_first}(tmp${previousAssoc});
  </#if>
  ${identifier} tmp${identifier} = builder.build();
  ${identifier} ${identifier?uncap_first} = ${identifier?uncap_first}DWO.create(tmp${identifier});
  assertNotNull(${identifier?uncap_first});
 } catch (ValidationException e){
   fail(e.getMessage());
 }

}
