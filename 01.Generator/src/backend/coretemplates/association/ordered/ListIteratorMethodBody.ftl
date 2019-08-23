<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates the default method body for the listIterator method

  @author: SE RWTH Aachen
-->
${tc.signature("attrName")}

{
  return org.apache.commons.collections4.iterators.UnmodifiableListIterator
    .umodifiableListIterator(this.${attrName}.listIterator());
}
