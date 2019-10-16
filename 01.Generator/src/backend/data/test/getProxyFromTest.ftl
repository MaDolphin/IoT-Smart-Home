<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  The init method in a unit test

  @author: SE RWTH Aachen
-->
${signature("identifier","visibleFields")}
{
try {
  <#assign typeHelper = tc.instantiate("common.util.TypeHelper")>
  <#assign getterSetterHelper = tc.instantiate("common.util.GetterSetterHelper")>
  // Builder for current class
  ${identifier}Builder builder = new ${identifier}Builder();
  ${identifier}DummyCreator creator = get${identifier}DummyCreator();

<#list visibleFields as field>
  <#assign fieldType = field.getType()>
  <#if typeHelper.isOptional(fieldType)>
  builder.${field.getName()}(creator.get${field.getName()?cap_first}());
  <#else>
  builder.${field.getName()}(creator.${getterSetterHelper.getGetter(field)?uncap_first}());
  </#if>
</#list>

  ${identifier} tmp${identifier} = builder.build();
  String ${identifier?uncap_first} = JsonMarshal.getInstance().marshal(tmp${identifier});
  assertNotNull(${identifier?uncap_first});
 } catch (ValidationException e){
   fail(e.getMessage());
 }

}
