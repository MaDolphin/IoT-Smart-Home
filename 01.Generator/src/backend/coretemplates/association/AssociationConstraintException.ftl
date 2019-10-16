<#-- (c) https://github.com/MontiCore/monticore -->
${signature("methodName", "one")}
<#assign h = tc.instantiate("common.util.TransformationUtils")>
throw new DataConsistencyException("${h.getErrorCode()}: ${methodName}: Cardinality violation <#if one>[1]<#else>[1..*]</#if>");
