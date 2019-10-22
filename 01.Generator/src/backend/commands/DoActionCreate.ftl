<#-- (c) https://github.com/MontiCore/monticore -->

${tc.signature("identifier", "className")}
{
return new IdDTO(daoLib.get${identifier}DAO().create(object, securityHelper.getSessionCompliantResource()).getId());
}