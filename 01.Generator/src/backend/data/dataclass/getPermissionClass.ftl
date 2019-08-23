<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  @author: SE RWTH Aachen
-->
${signature("clazz", "name")}
{
  return
  <#if clazz.getSuperclassOpt().isPresent()>
      super.getPermissionClass();
  <#else>
      "${name?uncap_first}";
  </#if>
}
