<#-- (c) https://github.com/MontiCore/monticore -->

${tc.signature("identifier")}
{
return new ${identifier}FullDTOLoader(daoLib, objectId, securityHelper).getDTO();
}