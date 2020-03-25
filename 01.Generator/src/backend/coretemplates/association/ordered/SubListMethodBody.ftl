<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates the method body for getting the sublist from a start to an end

-->
${tc.signature("varName")}

{
  return ImmutableList.copyOf(this.${varName}.subList(start,end));
}
