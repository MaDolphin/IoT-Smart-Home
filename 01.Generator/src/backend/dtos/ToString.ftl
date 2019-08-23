<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("dtoClass", "attributes")}
<#assign typeHelper = tc.instantiate("common.util.TypeHelper")>
<#assign getterSetterHelper = tc.instantiate("common.util.GetterSetterHelper")>
{
   StringBuilder builder = new StringBuilder();
   builder.append("${dtoClass} [");
<#list attributes as attribute>
   <#assign attrType = attribute.getType()>
   <#assign attrName = attribute.getName()>
   <#assign attrNameSuffix = getterSetterHelper.getNameSuffix(attribute)>
   builder.append(" ${attrName}=");
   builder.append(this.${attrName});
</#list>
   builder.append("]");

   return builder.toString();
}
