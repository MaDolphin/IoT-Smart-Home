<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates method body for an ordered add all method

  @author: SE RWTH Aachen
-->
${tc.signature("attrType", "attrName", "rawMethodCall")}

{  
  this.${attrName}.addAll(index, ${attrName});
}
