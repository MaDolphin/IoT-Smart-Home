<#-- (c) https://github.com/MontiCore/monticore -->
<#--
-->
${signature("clazz", "permissionClass", "default")}
{
return
<#if permissionClass.isPresent()>
  ObjectClasses.${permissionClass.get()}.getIdentifier();
<#else>
    <#if clazz.getSuperclassOpt().isPresent()>
      super.getPermissionClass();
    <#else>
      ObjectClasses.${default}.getIdentifier();
    </#if>
</#if>
}
