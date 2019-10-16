<#-- (c) https://github.com/MontiCore/monticore -->

${tc.signature("identifier", "className")}
{
QueryRestriction restriction = new QueryRestriction(daoLib.get${identifier}DAO());
restriction.setLimit(this.limit);

return new ${identifier}FullDTOListLoader().loadAllWithRestriction(daoLib, restriction, securityHelper);
}
