<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attrType", "removeMethodName", "removeAttrList")}

{
  boolean success = false;
  for(${attrType} element: ${removeAttrList}){
    success &= ${removeMethodName}(element);
  } 
  return success;
}
