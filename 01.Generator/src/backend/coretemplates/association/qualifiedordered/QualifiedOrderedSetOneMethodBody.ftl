<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attrType", "attrName", "qualifierType", "qualifierName")}

{  
  java.util.ArrayList<${qualifierType}> aryKey = new java.util.ArrayList<${qualifierType}>(this.${attrName}.keySet());
  java.util.ArrayList<${attrType}> aryVal = new java.util.ArrayList<${attrType}>(this.${attrName}.values());

  ${attrType} oldObj = aryVal.get(index);
  String oldQual = aryKey.get(index);

  if (oldObj != null && oldQual != null) {
    aryKey.set(index, ${qualifierName});
    aryVal.set(index, ${attrName});

    this.${attrName}.put(oldQual, oldObj);

  }
}
