<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attrList")}
{
  DTOTypeResolver.types = new Map();
  DTOTypeResolver.types.set('ErrorDTO', ErrorDTO);
  DTOTypeResolver.types.set('OkDTO', OkDTO);
  DTOTypeResolver.types.set('IdDTO', IdDTO);
  DTOTypeResolver.types.set('NotImplementedDTO', NotImplementedDTO);
  <#list attrList as attr>
    DTOTypeResolver.types.set('${attr}DTO', ${attr}DTO);
  </#list>
  return DTOTypeResolver.types;
}
