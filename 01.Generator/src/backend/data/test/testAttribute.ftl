<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  The method in a unit test class to test a property.

-->
${signature("field", "default")}
<#assign getterSetterHelper = tc.instantiate("common.util.GetterSetterHelper")>
{
      clazz.${getterSetterHelper.getSetter(field)}(${default});
      assertEquals(${default}, clazz.${getterSetterHelper.getGetter(field)}());
}

