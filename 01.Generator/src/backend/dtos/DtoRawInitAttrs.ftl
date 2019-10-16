<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("superArguments", "attributes", "clazzName")}
<#assign getterSetterHelper = tc.instantiate("common.util.GetterSetterHelper")>
{

<#if superArguments?size == 0>
        // no call of super method
<#else>
        super.rawInitAttrs( ${superArguments?join(",")} );
</#if>

<#if superArguments?size == 0>
        this.setId(id);
</#if>
<#list attributes as attribute>
        this.${getterSetterHelper.getSetter(attribute)}(${attribute.getName()});
</#list>

    return (${clazzName})this;
    
}
