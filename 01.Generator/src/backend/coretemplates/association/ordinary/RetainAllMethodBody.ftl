<#-- (c) https://github.com/MontiCore/monticore -->
${signature("collectionName")}
{
  Sets.difference(Sets.newHashSet(${collectionName}), Sets.newHashSet(o)).forEach(s -> remove${collectionName?cap_first}(s));
  //this.${collectionName}.retainAll(o);
}
