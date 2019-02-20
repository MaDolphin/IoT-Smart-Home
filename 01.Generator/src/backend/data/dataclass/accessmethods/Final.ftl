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
  Generates the method body of final attributes. For each final attribute an additional attribute is generated
  that stores if the original attribute value has been set. Once the value has been set the additionally generated
  attribute is set to "true", which results in the effect that the original attribute value cannot be changed.

  @author: SE RWTH Aachen
-->
${tc.signature("finalAttrName", "attrName", "newAttrName")}
{
  <#assign thisPrefix = "">
  <#if !ast.getModifier().isStatic()>
   <#assign thisPrefix = "this.">
  </#if> 
  
  <#assign localFinalAttrName = thisPrefix + finalAttrName>
  
  if (!${localFinalAttrName}){    
    ${localFinalAttrName} = true;
    ${thisPrefix}${attrName} = ${newAttrName} ;
  }  
}
