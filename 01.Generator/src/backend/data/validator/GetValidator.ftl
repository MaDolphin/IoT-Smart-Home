<#-- (c) https://github.com/MontiCore/monticore -->
<#--
-->
${signature("typeName")}

{
  if (null == validator) {
    validator = new ${typeName}();
  }
  return validator;
}
