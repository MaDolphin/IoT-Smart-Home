<#-- (c) https://github.com/MontiCore/monticore -->
{
  // return default value
  <#assign typeHelper = tc.instantiate("common.util.TypeHelper")>
  return ${typeHelper.getDefaultValue(ast.printReturnType())};
}
