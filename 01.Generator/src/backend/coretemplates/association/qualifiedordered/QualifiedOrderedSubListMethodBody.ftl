<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("type", "name", "qualifierName")}

{
    return ImmutableList.copyOf((this.${name}.get(${qualifierName}))).subList(start, end);
}
