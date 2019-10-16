<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Get method for derived attributes. Try to get the value and if it is not implemented,
  then use a default value.

  @author: SE RWTH Aachen
-->
${signature("getMethodName", "defaultValue")}

{
  if (object.isPresent()){
    try{
      return object.get().${getMethodName}();
    } catch(org.apache.commons.lang.NotImplementedException e){ }
  }

  return ${defaultValue};
}
