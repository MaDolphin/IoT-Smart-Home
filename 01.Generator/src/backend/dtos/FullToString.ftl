<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("assocs")}
<#assign assocHelper = tc.instantiate("common.util.CDAssociationUtil")>
{
  StringBuilder builder = new StringBuilder(super.toString());
  builder.append("[");
<#list assocs as assoc>
  <#assign assocName=assocHelper.getAssociationName(assoc)>
  <#if assocHelper.isMultiple(assoc)>
    builder.append(" ${assocName}=");
    builder.append(this.${assocName}List);
  <#else>
    builder.append(" ${assocName}=");
    builder.append(this.${assocName});
  </#if>
</#list>
  builder.append("]");
  return builder.toString();
}
