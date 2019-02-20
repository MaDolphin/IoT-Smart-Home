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
{
  <#assign returnStatement = false>
  <#assign methodName = "${ast.name}">

  <#if !( (methodName?starts_with("add") && ast.getCDParameters()?size gt 1)
    || methodName?starts_with("clear") || methodName?starts_with("set") || methodName?starts_with("rawSet")
    || methodName?starts_with("retainAll") || (methodName?starts_with("put") && ast.getCDParameters()?size == 3)
    || methodName?starts_with("rawUnset"))>
    <#assign returnStatement = true>
  </#if>

  <#if methodName?starts_with("raw")>
    if (this.object.isPresent()){
    <#if returnStatement>
      return
    </#if>
      this.object.get().${methodName}(
        <#list ast.getCDParameters() as p>
          ${p.name} <#if p_has_next>,</#if>
        </#list>
      );
    } else {
      <#if returnStatement>
        return
      </#if>
        super.${methodName}(
          <#list ast.getCDParameters() as p>
          ${p.name} <#if p_has_next>,</#if>
          </#list>
        );
    }

  <#else>
    <#if methodName?starts_with("add") || methodName?starts_with("set") || methodName?starts_with("put")>
      if (initiated){
    </#if>

    loadObjectIfNecessary();
    <#if returnStatement>
      return
    </#if>

    this.object.get().${ast.getName()}(<#list ast.getCDParameters() as param>${param.getName()}<#if param_has_next>, </#if></#list>);

    <#if methodName?starts_with("add") || methodName?starts_with("set") || methodName?starts_with("put")>
      } else {
      <#if returnStatement>
        return
      </#if>
      super.${methodName}(
        <#list ast.getCDParameters() as p>
          ${p.name} <#if p_has_next>,</#if>
        </#list>
      );
      }
    </#if>
  </#if>
}
