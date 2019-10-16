<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attrName", "lowerBoundCheck")}

{
  return Iterators.unmodifiableIterator(this.${attrName}.keySet().iterator());
}
