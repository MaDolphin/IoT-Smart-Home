<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  @author: SE RWTH Aachen
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
