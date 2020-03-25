<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates a Java interface

-->
${tc.signature("packageName", "importList")}

${defineHookPoint("InterfaceContent:addComment")}

package ${packageName};

<#list importList as i>
  import ${i};
</#list>

${defineHookPoint("<Block>?InterfaceContent:addImports")}

<#-- Annotations hook -->
${defineHookPoint("<Block>?InterfaceContent:addAnnotations")}

${ast.printModifier()} interface ${ast.getName()}
 
<#-- print implements -->
<#if ast.getInterfaceList()?size gt 0>
  extends ${ast.printInterfaces()}
</#if>
{
  <#-- generate all methods -->
  <#list ast.getCDMethodList() as method>
    ${tc.includeArgs("backend.coretemplates.InterfaceMethod", [method])}
  </#list>
  
  <#-- member HOOK -->
  ${defineHookPoint("<Block>?InterfaceContent:Members")}
}
