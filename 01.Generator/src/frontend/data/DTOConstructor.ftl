<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributes", "hasSpecificSuperclass", "className", "useTypeof")}
{
    super(<#if hasSpecificSuperclass>dto</#if>);
    if (dto<#if useTypeof> && dto instanceof ${className}</#if>) {
<#list attributes as attr>
        this.${attr.getName()} = dto.${attr.getName()};
</#list>
    }

    this.typeName = '${className}';
}
