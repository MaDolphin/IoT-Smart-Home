<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("proxyClass", "attributes")}
<#assign typeHelper = tc.instantiate("common.util.TypeHelper")>
<#assign getterSetterHelper = tc.instantiate("common.util.GetterSetterHelper")>
{
   StringBuilder builder = new StringBuilder();
   builder.append("${proxyClass} [id=");
   builder.append(getId());
<#list attributes as attribute>
   <#assign attrType = attribute.getType()>
   <#assign attrName = attribute.getName()>
   <#assign attrNameSuffix = getterSetterHelper.getNameSuffix(attribute)>
   builder.append(", ${attrName}=");
   <#if typeHelper.isBoolean(attrType)>
   builder.append(this.is${attrNameSuffix?cap_first}());
   <#else>
   builder.append(this.get${attrName?cap_first}());
   </#if>
</#list>
   builder.append("]");

   return builder.toString();
}
