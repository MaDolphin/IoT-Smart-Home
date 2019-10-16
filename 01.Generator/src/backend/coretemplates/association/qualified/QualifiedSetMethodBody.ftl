<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("mapName", "qualifierName", "objectName", "rawMethodCall")}

{
    <#if rawMethodCall??>
      ${objectName}.${rawMethodCall};
    </#if>
  
    this.${mapName}.put(${qualifierName}, ${objectName});
}
