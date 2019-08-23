<#-- (c) https://github.com/MontiCore/monticore -->

// Converts Data Transfer Object (DTO) to a normal Data Object
${tc.signature("clazz", "dtoClass", "attributes", "dtoAttributes")}
<#assign typeHelper = tc.instantiate("common.util.TypeHelper")>
<#assign getterSetterHelper = tc.instantiate("common.util.GetterSetterHelper")>
<#assign astPrinter = tc.instantiate("de.monticore.umlcd4a.prettyprint.AstPrinter")>
<#assign identifier = dtoClass?keep_before("DTO")>
{
    ${identifier}Builder builder = new ${identifier}Builder();

    <#list attributes as attr>

        <#assign attrName = attr.getName()>
        <#assign attrNameSuffix = getterSetterHelper.getNameSuffix(attr)>

        <#if typeHelper.isOptional(attr.getType())>
            if (${getterSetterHelper.getGetNamePrefix(attr)}${attrName?cap_first}() != null) {
                builder.${attrName}(Optional.of(${getterSetterHelper.getGetNamePrefix(attr)}${getterSetterHelper.getNameSuffix(attr)?cap_first}()));
            }
            else{
                builder.${attrName}(Optional.empty());
            }
        <#else>
            builder.${attrName}(${getterSetterHelper.getGetNamePrefix(attr)}${getterSetterHelper.getNameSuffix(attr)?cap_first}());
        </#if>

    </#list>

    <#--builder.budget(setBudgetIfPresent(bdao));
    builder.externalAccount(setExternalAccountIfPresent(adao));-->
    /* Build Data Object */
    ${identifier?cap_first} ${identifier?uncap_first} = builder.build();
    Log.debug("getDataClass: " + ${identifier?uncap_first}, getClass().getName());

    return ${identifier?uncap_first};
}
