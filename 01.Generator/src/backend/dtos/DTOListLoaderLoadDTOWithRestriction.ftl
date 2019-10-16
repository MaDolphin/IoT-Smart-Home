<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("originalName", "suffix")}

{
// TODO: only load permitted objects
List<${originalName}> ${originalName?uncap_first}s = daoLib.get${originalName}DAO().loadAllWithRestriction(restriction, daoLib, securityHelper.getSessionCompliantResource());

return new ${originalName}${suffix}FullDTOList(${originalName?uncap_first}s);

}
