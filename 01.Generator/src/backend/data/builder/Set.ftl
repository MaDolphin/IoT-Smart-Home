<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates the constructor for objects.

-->
${tc.signature("attr", "typeHelper")}
{
  <#if typeHelper.isOptional(attr.getType())>
    if (${attr.getName()} == null) {
      ${attr.getName()} = Optional.empty();
    }
  <#elseif typeHelper.isList(attr.getType())>
    if (${attr.getName()} == null) {
      ${attr.getName()} = new LinkedList();
    }
  </#if>

  this.${attr.getName()}= ${attr.getName()};
  return this;
}
