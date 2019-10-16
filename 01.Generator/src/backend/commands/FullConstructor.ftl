<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributes")}
{
    this();

<#list attributes as attr>
        this.${attr.getName()} = ${attr.getName()};
</#list>
}
