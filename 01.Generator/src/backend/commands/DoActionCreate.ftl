<#-- (c) https://github.com/MontiCore/monticore -->

${tc.signature("identifier", "className")}
{
${identifier} created = daoLib.get${identifier}DAO().create(object, securityHelper.getSessionCompliantResource());
if (securityHelper.grantCurrentUserRoleAndPermission(Roles.ADMIN, created.getPermissionClass(), created.getId(), null)) {
Log.debug("MAB0x9040: ${className}.doAction: created object with id: " + created.getId(), "${className}");
return new IdDTO(created.getId());
}
else {
return new ErrorDTO("MAB0x9040", MontiGemErrorFactory.unknown("Could not grant right to read new Object"));
}
}