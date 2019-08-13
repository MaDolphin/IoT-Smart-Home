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
  The core method to validate if an object is consistent.
  I.e. all OCL contrsints and all basic constraints will be checked.

  @author: SE RWTH Aachen
-->
${signature("identifier", "visibleFields", "checkAssocs", "hasSuperclass", "nonAttributeOclConstraints")}
<#assign param = identifier?uncap_first>

{
  StringBuilder msgs = new StringBuilder();
  msgs.append(getErrors(isNotNull(${param})));

<#if hasSuperclass>
   super.getValidationErrors(${param}).ifPresent(msgs::append);
</#if>

  // handle visible attributes
<#list visibleFields as field>
  <#if !field.isDerived()>
  msgs.append(getErrors(is${field.getName()?cap_first}Valid(${param})));
  </#if>
</#list>

  // handle associations
<#list checkAssocs as a>
  <#if !a.isDerived()>
  msgs.append(getErrors(is${a.getDerivedName()?cap_first}Valid(${param})));
  </#if>
</#list>

  // handle OCL constraints (non attribute related constraints)
<#list nonAttributeOclConstraints as method>
  msgs.append(getErrors(${method.getName()}(${param})));
</#list>

  if (msgs.length() != 0) {
    String header = ${param}.getHumanName() + " ist nicht korrekt:\n";
    Log.warn(header + msgs + "\n" + ${param}.toString());
    return Optional.of(header + msgs);
  }

  return Optional.empty();
}
