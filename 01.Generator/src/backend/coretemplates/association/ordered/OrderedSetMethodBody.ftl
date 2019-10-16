<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attrType", "attrName")}

{  
  ${attrType} target = this.${attrName}.get(index);
   
  this.${attrName}.set(index, o);
  this.${attrName}.add(target);
}
