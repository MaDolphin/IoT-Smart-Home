<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attrType", "attrName")}
{
  return new java.util.ArrayList<${attrType}>(this.${attrName}.values()).get(index);
}
