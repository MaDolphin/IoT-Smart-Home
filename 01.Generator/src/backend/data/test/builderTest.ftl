<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  The init method in a unit test

  @author: SE RWTH Aachen
-->
${signature("identifier", "visibleFields", "assocs")}
<#assign cdassoc = tc.instantiate("common.util.CDAssociationUtil")>
<#assign typeHelper = tc.instantiate("common.util.TypeHelper")>
<#assign getterSetterHelper = tc.instantiate("common.util.GetterSetterHelper")>
<#assign assocUtil = tc.instantiate("common.util.CDAssociationUtil")>
{
  ${identifier}Builder builder = new ${identifier}Builder();
  ${identifier}DummyCreator creator = get${identifier}DummyCreator();
    // handle visible attributes
  <#list visibleFields as field>
    <#assign fieldType = field.getType()>
    <#if typeHelper.isOptional(fieldType)>
       builder.${field.getName()}(creator.get${field.getName()?cap_first}());
    <#else>
      builder.${field.getName()}(creator.${getterSetterHelper.getGetter(field)?uncap_first}());
    </#if>
</#list>
<#list assocs as assoc>
 <#assign targetType = assoc.getTargetType().getName()?cap_first>
 <#assign methodName = assoc.getDerivedName()?uncap_first>
 <#assign creatorClass = targetType + "DummyCreator">
 <#assign dummy = "dummy" + methodName?cap_first + "Creator">

 ${creatorClass} ${dummy} = get${dummy?cap_first}();
 <#if assocUtil.isMultiple(assoc) >
   builder.${methodName}(Lists.newArrayList(${dummy}.get${targetType}()));
 <#elseif !cdassoc.isOptional(assoc) >
   builder.${methodName}(${dummy}.get${targetType}());
 <#else>
   builder.${methodName}(Optional.of(${dummy}.get${targetType}()));
 </#if>
</#list>
 try {
    assertNotNull(builder.build());
 }
 catch (ValidationException e) {
    fail(e.getMessage());
 }
}
