<#-- (c) https://github.com/MontiCore/monticore -->
<#--
-->
${signature("clazz")}
{
  return
  <#if clazz.getSuperclassOpt().isPresent()>
      super.getPermissionId();
  <#else>
      this.id;
  </#if>
}
