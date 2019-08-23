<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attrName", "qualifierName")}
{
    return UnmodifiableListIterators.unmodifiableListIterator(this.${attrName}.get(${qualifierName}).listIterator());
}
