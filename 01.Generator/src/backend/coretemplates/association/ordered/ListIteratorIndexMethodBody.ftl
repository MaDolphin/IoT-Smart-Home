<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates the default method body for the ListIterator method with an index

-->
${tc.signature("attrName")}

{
  return org.apache.commons.collections4.iterators.UnmodifiableListIterator
    .umodifiableListIterator(this.${attrName}.listIterator(index));
}
