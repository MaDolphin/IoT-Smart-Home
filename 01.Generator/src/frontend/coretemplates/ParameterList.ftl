<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates a Java method

-->
${tc.signature("method")}
<#assign astPrinter = tc.instantiate("de.monticore.umlcd4a.prettyprint.AstPrinter")>
<#list method.getCDParameterList() as parameter>${parameter.getName()}: ${astPrinter.printType(parameter.getType())}<#if parameter_has_next>,</#if></#list>
