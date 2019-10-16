<#-- (c) https://github.com/MontiCore/monticore -->


${tc.signature("symTab", "dtoClass", "domainClass", "attributes")}
<#assign typeHelper = tc.instantiate("common.util.TypeHelper")>
<#assign getterSetterHelper = tc.instantiate("common.util.GetterSetterHelper")>
<#assign astPrinter = tc.instantiate("de.monticore.umlcd4a.prettyprint.AstPrinter")>
<#assign utils = tc.instantiate("common.util.TransformationUtils")>
<#assign identifier = domainClass>
{
${identifier}Builder builder = new ${identifier}Builder();
builder.id(this.getId());

<#list attributes as attr>
    <#assign attrType = attr.getType()>
    <#assign attrName = attr.getName()>

    <#if typeHelper.isOptional(attr.getType())>
            if (${attrName} != null) {
                builder.${attrName}(Optional.of(${attrName}));
            }
            else{
                builder.${attrName}(Optional.empty());
            }
    <#elseif utils.isDTOType(symTab, attr.getType())>
            builder.${attrName}(this.${getterSetterHelper.getGetter(attr)}().toBuilder().build());
    <#else>
            builder.${attrName}(this.${getterSetterHelper.getGetter(attr)}());
    </#if>
</#list>

    // return the builder for further processing
    return builder;
}
