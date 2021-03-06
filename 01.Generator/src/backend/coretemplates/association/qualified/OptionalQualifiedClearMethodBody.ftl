<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attrType", "qualifierType", "attrName", "removeMethodName")}

{
  for (java.util.Map.Entry<${qualifierType}, java.util.Optional<${attrType}>> element: com.google.common.collect.Lists.newArrayList(this.${attrName}.entrySet())){
    ${removeMethodName}(element.getKey());
  }  
}
