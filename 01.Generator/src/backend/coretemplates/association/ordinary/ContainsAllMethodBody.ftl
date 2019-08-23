<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attrName", "newAttrName", "isOneToMany")}

{
  return this.${attrName}.containsAll(${newAttrName});
}
