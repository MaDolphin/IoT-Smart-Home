<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attrType", "attrName", "removeMethodName")}

{
  for (${attrType} element: com.google.common.collect.Lists.newArrayList(this.${attrName})){
    ${removeMethodName}(element);
  }  
}
