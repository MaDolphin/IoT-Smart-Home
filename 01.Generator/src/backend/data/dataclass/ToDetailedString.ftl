<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("handledClass", "attributes", "superclassPresent")}
<#assign typeHelper = tc.instantiate("common.util.TypeHelper")>
<#assign getterSetterHelper = tc.instantiate("common.util.GetterSetterHelper")>
{
   StringBuilder builder = new StringBuilder();
   builder.append("${handledClass} [");
<#if superclassPresent>
    builder.append(super.toDetailedString());
</#if>
<#list attributes as attribute>
   <#assign attrType = attribute.getType()>
   <#assign attrName = attribute.getName()>
   <#assign attrNameSuffix = getterSetterHelper.getNameSuffix(attribute)>
   builder.append(" ${attrName}=");
   <#if typeHelper.isBoolean(attrType)>
   builder.append(this.${attrName});
   <#else>
   builder.append(this.${attrName});
   </#if>
</#list>
   builder.append("]");

   return builder.toString();
}
