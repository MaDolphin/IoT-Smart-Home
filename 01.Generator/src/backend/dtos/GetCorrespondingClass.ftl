<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates the default method body for every get method of an attribute.

  @author: SE RWTH Aachen
-->
{
  try {
    return Optional.of(CommandResultType.valueOf(name));
  } catch (IllegalArgumentException e) {
    return Optional.empty();
  }
}
