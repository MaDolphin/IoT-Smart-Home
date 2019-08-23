<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attrName", "qualifierName", "newAttrName")}

{
  if (this.${attrName}.containsKey(key)) {
    return this.${attrName}.get(key).contains(${newAttrName});
  }
  
  return false;
}
