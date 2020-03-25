<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates a Java interface

-->
${tc.signature("packageName", "importList")}

${defineHookPoint("InterfaceContent:addComment")}

<#list importList as i>
  import ${i};
</#list>

${defineHookPoint("<Block>?InterfaceContent:addImports")}

<#-- Annotations hook -->
${defineHookPoint("<Block>?InterfaceContent:addAnnotations")}

export ${ast.printModifier()} interface ${ast.getName()}
 
<#-- print implements -->
<#if ast.getInterfaceList()?size gt 0>
  extends ${ast.printInterfaces()}
</#if>
{
  <#-- generate all attributes -->
  <#list ast.getCDAttributeList() as attribute>
    ${tc.includeArgs("frontend.coretemplates.Attribute", [attribute])}
  </#list>

  <#-- generate all methods -->
  <#list ast.getCDMethodList() as method>
    ${tc.includeArgs("frontend.coretemplates.InterfaceMethod", [method])}
  </#list>
  
  <#-- member HOOK -->
  ${defineHookPoint("<Block>?InterfaceContent:Members")}
}
