<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates the constructor for objects.

-->
${tc.signature("clazz", "identifier", "parentAttrList", "parentAssocList", "localAttrList", "localAssocList", "assocNameUtil", "assocUtil")}
<#assign getterSetterHelper = tc.instantiate("common.util.GetterSetterHelper")>

{
  if (nv instanceof ${identifier}) {
    ${identifier} newValues = (${identifier}) nv;

  <#list parentAttrList as attr>
    <#if !attr.isDerived()>
        ${getterSetterHelper.getPlainSetter(attr)}(newValues.${getterSetterHelper.getPlainGetter(attr)}());
    </#if>
  </#list>

  <#list parentAssocList as assoc>
    <#if !assoc.isDerived()>
        <#if assocUtil.isMultiple(assoc)>
            ${assocNameUtil.getSetAllMethodName(assoc).orElse("")}(newValues.${assocNameUtil.getGetAllMethodName(assoc).orElse("")}());
        <#else>
      set${assoc.getDerivedName()?cap_first}(newValues.get${assoc.getDerivedName()?cap_first}());
        </#if>
    </#if>
  </#list>
  

  <#list localAttrList as attr>
    <#if !attr.isDerived()>
      ${getterSetterHelper.getPlainSetter(attr)}(newValues.${getterSetterHelper.getPlainGetter(attr)}());
    </#if>
  </#list>

  <#list localAssocList as assoc>
    <#if !assoc.isDerived()>
    <#if assocUtil.isMultiple(assoc)>
      ${assocNameUtil.getSetAllMethodName(assoc).orElse("")}(newValues.${assocNameUtil.getGetAllMethodName(assoc).orElse("")}());
    <#else>
      set${assoc.getDerivedName()?cap_first}(newValues.get${assoc.getDerivedName()?cap_first}());
    </#if>
    </#if>
  </#list>
  } else {
      throw new IllegalArgumentException();
  }
}
