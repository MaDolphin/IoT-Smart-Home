<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attrType", "attrName", "qualifierName")}

{
  return new java.util.ArrayList<${attrType}>(this.${attrName}.keySet()).lastIndexOf(${qualifierName});
}
