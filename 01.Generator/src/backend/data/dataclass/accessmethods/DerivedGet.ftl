<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates the method body for derived attribute getters. By default it has to throw an exception, as
  it is unknown how the value of the derived attribute is computed.

  @author: SE RWTH Aachen
-->
${tc.signature("methodName", "identifier")}
{
  throw new org.apache.commons.lang.NotImplementedException("The method body of ${methodName} is not implemented because the" 
	+"underlying value is derived. Please make sure to create a handcoded extension of the class ${identifier} and"
    +"provide a suitable implementation for this method.");
}
