<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates Java-enums from CDEnums including an extension point for each 
  content of an enum (enum-constants, attributes, constructors, methods, 
  and associations).
  
-->
${tc.signature("package", "importList")}
<#assign util = tc.instantiate("common.util.TransformationUtils")>
${defineHookPoint("EnumContent:addComment")}

package ${package};

<#list importList as i>
  import ${i};
</#list>

${ast.printModifier()} enum ${ast.getName()}

<#-- print implements -->
<#if ast.getInterfaceList()?size gt 0>
  implements ${ast.printInterfaces()}
</#if>
{

${util.printCDEnumConstants(ast.getCDEnumConstantList())};

<#-- generate all attributes -->
<#list ast.getCDAttributeList() as attribute>
    ${tc.includeArgs("backend.coretemplates.Attribute", [attribute])}
</#list>

<#-- generate all constructors -->
<#list ast.getCDConstructorList() as constr>
    ${tc.includeArgs("backend.coretemplates.Constructor", [constr])}
</#list>

<#-- generate all methods -->
<#list ast.getCDMethodList() as method>
    ${tc.includeArgs("backend.coretemplates.Method", [method])}
</#list>
}
