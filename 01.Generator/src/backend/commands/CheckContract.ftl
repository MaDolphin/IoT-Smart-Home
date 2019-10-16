<#-- (c) https://github.com/MontiCore/monticore -->

${tc.signature("className", "attrs")}
{
<#list attrs as attr>
  if (this.${attr.getName()} == null) {
  Log.info("0xB9030: given type is null", "${className}");
  return Optional.of(new ErrorDTO("0xB9030", MontiGemErrorFactory.missingField("${attr.getName()}")));
  }
</#list>

return Optional.empty();
}