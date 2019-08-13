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
  Generates a Java class

  @author: SE RWTH Aachen
-->

${tc.signature("packageName", "importList")}
${defineHookPoint("ClassContent:addComment")}

${tc.includeArgs("backend.coretemplates.Copyright")}
<#-- set package -->
package ${packageName};

<#-- handle imports from model -->
<#list importList as i> import ${i}; </#list>

<#-- Imports hook --> 
${defineHookPoint("<Block>?ClassContent:addImports")}

<#-- Annotations hook -->
${defineHookPoint("<Block>?ClassContent:addAnnotations")}

${ast.printAnnotation()}
${ast.printModifier()} class ${ast.getName()} ${tc.include("backend.coretemplates.Generics")}

<#-- print extends -->
<#if ast.getSuperclassOpt().isPresent()>
  extends ${ast.printSuperClass()}
</#if>

<#-- print implements -->
<#if ast.getInterfaceList()?size gt 0>
  implements ${ast.printInterfaces()}
</#if>

{
  <#-- generate all attributes -->  
  <#list ast.getCDAttributeList() as attribute>
    ${tc.includeArgs("backend.coretemplates.Attribute", [attribute])}
  </#list>
  
  <#-- generate all constructors -->
  <#list ast.getCDConstructorList() as constructor>
    ${tc.includeArgs("backend.coretemplates.Constructor", [constructor])}
  </#list>
  
  <#-- generate all methods -->
  <#list ast.getCDMethodList() as method>
    ${tc.includeArgs("backend.coretemplates.Method", [method])}
  </#list>

  <#-- member HOOK -->
  ${defineHookPoint("<Block>?ClassContent:Members")}
}
