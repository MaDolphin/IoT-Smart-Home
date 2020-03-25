<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates the constructor for objects.

-->
${tc.signature("clazz", "identifier", "parentAttrList", "localAttrList")}
<#assign getterSetterHelper = tc.instantiate("common.util.GetterSetterHelper")>

    {
    if (nv instanceof ${identifier}) {
${identifier} newValues = (${identifier}) nv;

<#list parentAttrList as attr>
    <#if !attr.isDerived()>
        ${getterSetterHelper.getPlainSetter(attr)}(newValues.${getterSetterHelper.getPlainGetter(attr)}());
    </#if>
</#list>

<#list localAttrList as attr>
    <#if !attr.isDerived()>
        ${getterSetterHelper.getPlainSetter(attr)}(newValues.${getterSetterHelper.getPlainGetter(attr)}());
    </#if>
</#list>
} else {
throw new IllegalArgumentException();
}
}
