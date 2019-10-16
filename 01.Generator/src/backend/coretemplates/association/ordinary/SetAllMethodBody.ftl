<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attrName", "newAttrName")}

{
 if(this.${attrName} == null) {
  this.${attrName} = new ArrayList<>();
 } else {
  this.${attrName}.clear();
 }

 this.${attrName}.addAll(${newAttrName});
}
