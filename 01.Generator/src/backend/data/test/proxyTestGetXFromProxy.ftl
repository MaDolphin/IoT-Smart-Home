<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  The init method in a unit test

  @author: SE RWTH Aachen
-->
${signature("identifier", "assocs")}
{
    try {
        ${identifier} ${identifier?uncap_first} = a${identifier}Proxy.get${identifier}FromProxy(
        <#list assocs as assoc>
            <#assign derivedName = assoc.getDerivedName()?cap_first>
                ${derivedName?uncap_first}DWO
            <#if !assoc?is_last>, </#if>
        </#list>
        );
        Assert.assertNotNull(${identifier?uncap_first});
    } catch(Exception e){
        e.printStackTrace();
    }

}
