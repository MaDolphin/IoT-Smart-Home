<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attrName", "qualifierType", "qualifierName")}

{
  return new java.util.ArrayList<${qualifierType}>(this.${attrName}.keySet()).indexOf(${qualifierName});
}
