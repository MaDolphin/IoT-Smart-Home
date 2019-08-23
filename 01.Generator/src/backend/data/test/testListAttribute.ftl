<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  The method in a unit test class to test a property.

  @author: SE RWTH Aachen
-->
${signature("field")}
<#assign typeHelper = tc.instantiate("common.util.TypeHelper")>
<#assign fieldType = field.getType()>
<#assign fieldName = field.getName()>
<#assign getterSetterHelper = tc.instantiate("common.util.GetterSetterHelper")>
{
<#if typeHelper.isCollection(fieldType)>
      clazz.${getterSetterHelper.getSetter(field)}(new ArrayList<>());
      List<String> ${fieldName} = clazz.${getterSetterHelper.getGetter(field)}();
      Assert.assertTrue(${fieldName}.isEmpty());
</#if>
}

