<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  The init method in a unit test

-->
${signature("identifier","visibleFields", "oneAssocs", "fieldsOfAssocs")}
{
try {
    <#assign firstAssoc = "">
    <#assign previousAssoc = "">
    <#list oneAssocs?reverse as oneAssoc>
    ${oneAssoc}Builder builder${oneAssoc} = new ${oneAssoc}Builder();
        <#if previousAssoc != "">
    builder${oneAssoc}.${previousAssoc?uncap_first}(tmp${previousAssoc});
        </#if>
    ${oneAssoc}DummyCreator creator${oneAssoc} = get${oneAssoc}DummyCreator();

        <#if firstAssoc = ""><#assign firstAssoc = oneAssoc></#if>
        <#list fieldsOfAssocs as k>
            <#if k[0] == "#">
            <#assign class = k[1..]>
                <#else>
                <#if class == oneAssoc>
                    <#if k[0..2] == "get">
    builder${oneAssoc}.${k[3..]?uncap_first}(creator${oneAssoc}.${k}());
                    <#elseif k[0..1] == "is">
                      <#if k[2..4] == "Has">
    builder${oneAssoc}.has${k[5..]}(creator${oneAssoc}.${k}());
                      <#else>
    builder${oneAssoc}.is${k[2..]?cap_first}(creator${oneAssoc}.${k}());
                      </#if>
                    </#if>
                </#if>
            </#if>
        </#list>
    ${oneAssoc} tmp${oneAssoc} = builder${oneAssoc}.build();

    tmp${oneAssoc} = ${oneAssoc?uncap_first}DWO.create(tmp${oneAssoc});
    assertNotNull(tmp${oneAssoc});
        <#assign previousAssoc = oneAssoc>
    </#list>

  <#assign typeHelper = tc.instantiate("common.util.TypeHelper")>
  <#assign getterSetterHelper = tc.instantiate("common.util.GetterSetterHelper")>
  // Builder for current class
  ${identifier}Builder builder = new ${identifier}Builder();
  ${identifier}DummyCreator creator = get${identifier}DummyCreator();
  // handle visible attributes of current class
<#list visibleFields as field>
  <#assign fieldType = field.getType()>
  <#if typeHelper.isOptional(fieldType)>
  builder.${field.getName()}(creator.get${field.getName()?cap_first}());
  <#else>
  builder.${field.getName()}(creator.${getterSetterHelper.getGetter(field)?uncap_first}());
  </#if>
</#list>

  <#if previousAssoc != "">
    builder.${previousAssoc?uncap_first}(tmp${previousAssoc});
  </#if>
  ${identifier} tmp${identifier} = builder.build();
  ${identifier} ${identifier?uncap_first} = ${identifier?uncap_first}DWO.create(tmp${identifier});
  assertNotNull(${identifier?uncap_first});
 } catch (ValidationException e){
   fail(e.getMessage());
 }

}
