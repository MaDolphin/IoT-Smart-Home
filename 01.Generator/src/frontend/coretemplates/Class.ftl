<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates a Java class

  @author: SE RWTH Aachen
-->

${tc.signature("packageName", "importList")}
${defineHookPoint("ClassContent:addComment")}

${tc.includeArgs("backend.coretemplates.Copyright")}
<#-- handle imports from model -->
<#list importList as i>
  import ${i};
</#list>

<#-- Imports hook --> 
${defineHookPoint("<Block>?ClassContent:addImports")}

<#-- Annotations hook -->
${defineHookPoint("<Block>?ClassContent:addAnnotations")}

${ast.printAnnotation()}
export ${ast.printModifier()} class ${ast.getName()} ${tc.include("backend.coretemplates.Generics")}

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
    ${tc.includeArgs("frontend.coretemplates.Attribute", [attribute])}
  </#list>
  
  <#-- generate all constructors -->
  <#list ast.getCDConstructorList() as constructor>
    ${tc.includeArgs("frontend.coretemplates.Constructor", [constructor])}
  </#list>
  
  <#-- generate all methods -->
  <#list ast.getCDMethodList() as method>
    ${tc.includeArgs("frontend.coretemplates.Method", [method])}
  </#list>

  <#-- member HOOK -->
  ${defineHookPoint("<Block>?ClassContent:Members")}
}
