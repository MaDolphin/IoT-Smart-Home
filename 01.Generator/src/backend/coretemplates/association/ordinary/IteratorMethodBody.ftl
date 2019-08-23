<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attrName", "isOneToMany")}

{
  return Iterators.unmodifiableIterator(this.${attrName}.iterator());
}
