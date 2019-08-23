<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates the default set method for optional associations.

  @author: SE RWTH Aachen
-->

${tc.signature("param")}
<#assign typeHelper = tc.instantiate("common.util.TypeHelper")>
{
  if (${param} == null || !${param}.isPresent()) {
    return OptionalHelper.LONG_ABSENT_CONSTANT;
  }
  return ${param}.get().getId();
}
