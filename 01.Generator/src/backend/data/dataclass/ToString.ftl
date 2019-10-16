<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("handledClass", "attributes", "superclassPresent")}
<#assign typeHelper = tc.instantiate("common.util.TypeHelper")>
<#assign getterSetterHelper = tc.instantiate("common.util.GetterSetterHelper")>
{
   StringBuilder builder = new StringBuilder();
   builder.append("${handledClass} [");
<#if superclassPresent>
   builder.append(super.toString());
<#else>
   builder.append(" id=");
   builder.append(this.id);
</#if>
<#list attributes as field>
   <#assign attrType = field.getType()>
   <#if !typeHelper.isOptional(attrType.getName())>
     <#assign attrName = field.getName()>
   builder.append(" ${attrName}=");
     <#if typeHelper.isBoolean(attrType)>
   builder.append(this.${attrName});
     <#else>
   builder.append(this.${attrName});
     </#if>
   </#if>
</#list>
   builder.append("]");

   return builder.toString();
}
