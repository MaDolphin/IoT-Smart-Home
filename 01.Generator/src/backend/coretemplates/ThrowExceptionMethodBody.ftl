<#-- (c) https://github.com/MontiCore/monticore -->
${signature("methodName")}
<#assign h = tc.instantiate("common.util.TransformationUtils")>
{
  throw new org.apache.commons.lang.NotImplementedException("${h.getErrorCode()}: ${methodName}: The method body is not implemented."
	+"Please make sure to create a handcoded extension and"
    +"provide a suitable implementation for this method.");
}
