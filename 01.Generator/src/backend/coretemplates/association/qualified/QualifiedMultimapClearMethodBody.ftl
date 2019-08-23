<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attrType", "qualifierType", "attrName", "removeMethodName")}

{
  for (java.util.Map.Entry<${qualifierType}, java.util.Collection<${attrType}>> element : com.google.common.collect.Lists.newArrayList(this.${attrName}.asMap().entrySet())) {
    for (${attrType} innerElement : com.google.common.collect.Lists.newArrayList(element.getValue())) {
      remove${attrName?cap_first}(element.getKey(), innerElement);
    }
  }
}
