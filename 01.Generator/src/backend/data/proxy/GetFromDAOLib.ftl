<#-- (c) https://github.com/MontiCore/monticore -->


${tc.signature("proxyClass", "attributes")}
<#assign typeHelper = tc.instantiate("common.util.TypeHelper")>
<#assign getterSetterHelper = tc.instantiate("common.util.GetterSetterHelper")>
<#assign astPrinter = tc.instantiate("de.monticore.umlcd4a.prettyprint.AstPrinter")>
<#assign identifier = proxyClass?keep_before("Proxy")>
{
    ${identifier}Builder builder = new ${identifier}Builder();

    <#--builder.budget(setBudgetIfPresent(bdao));
    builder.externalAccount(setExternalAccountIfPresent(adao));-->
    /* Build Data Object */
    ${identifier?cap_first} ${identifier?uncap_first} = builder.build();
    Log.debug("getDataClass: " + ${identifier?uncap_first}, getClass().getName());

    return ${identifier?uncap_first};
}
