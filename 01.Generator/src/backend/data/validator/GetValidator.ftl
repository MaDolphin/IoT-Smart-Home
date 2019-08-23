<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  @author: SE RWTH Aachen
-->
${signature("typeName")}

{
  if (null == validator) {
    validator = new ${typeName}();
  }
  return validator;
}
