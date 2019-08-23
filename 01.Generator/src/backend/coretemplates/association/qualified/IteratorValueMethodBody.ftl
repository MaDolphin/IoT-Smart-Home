<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attrName", "lowerBoundCheck", "qualifierName")}

{
  if (this.${attrName}.get(${qualifierName})!=null){
      return Iterators.unmodifiableIterator(this.${attrName}.get(${qualifierName}).iterator());
  } else {
      return Collections.emptyIterator();
  }
}
