<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates the constructor for objects.

-->
${tc.signature("superArguments", "localAttributes", "localAssociations", "clazzName")}
<#assign typeHelper = tc.instantiate("common.util.TypeHelper")>
<#assign assocUtil = tc.instantiate("common.util.CDAssociationUtil")>

{
<#if superArguments?size == 0>
        // no call of super method
<#else>
        super.rawInitAttrs( ${superArguments?join(",")} );
</#if>


<#list localAttributes as attr>
    <#if !attr.isDerived()>
        <#if typeHelper.isOptional(attr.getType())>
        this.${attr.getName()} = ${attr.getName()}.orElse(null);
        <#else>
        this.${attr.getName()} = ${attr.getName()};
        </#if>
    </#if>
</#list>

<#list localAssociations as assoc>
    <#assign assocName=assocUtil.getAssociationName(assoc)?uncap_first>
    <#if assocUtil.isOptional(assoc)>
    this.${assocName} = ${assocName}.orElse(null);
    <#else>
    this.${assocName} = ${assocName};
    </#if>
</#list>

    return (${clazzName})this;
}
