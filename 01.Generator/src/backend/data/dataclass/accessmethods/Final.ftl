<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Generates the method body of final attributes. For each final attribute an additional attribute is generated
  that stores if the original attribute value has been set. Once the value has been set the additionally generated
  attribute is set to "true", which results in the effect that the original attribute value cannot be changed.

-->
${tc.signature("finalAttrName", "attrName", "newAttrName")}
{
  <#assign thisPrefix = "">
  <#if !ast.getModifier().isStatic()>
   <#assign thisPrefix = "this.">
  </#if> 
  
  <#assign localFinalAttrName = thisPrefix + finalAttrName>
  
  if (!${localFinalAttrName}){    
    ${localFinalAttrName} = true;
    ${thisPrefix}${attrName} = ${newAttrName} ;
  }  
}
