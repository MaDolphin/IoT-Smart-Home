<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("objectType", "mapName", "qualifierName", "attrName")}

{
  java.util.List<${objectType}> target = this.${mapName}.get(${qualifierName});

  if (target != null) {
    ${objectType} clone = target.get(index);
    target.set(index, o);
    target.add(clone);
  }
}
