<#-- (c) https://github.com/MontiCore/monticore -->

${tc.signature("className", "permission")}
{
if (!securityHelper.doesUserHavePermissionType(Permissions.${permission}, object.getPermissionClass(), object.getPermissionId())) {
Log.warn("${className} MAB0x9038: User doesn't have permission for " +
object.getPermissionClass() + "_" + Permissions.${permission});
return Optional.of(new ErrorDTO("MAB0x9038", MontiGemErrorFactory.forbidden()));
}

return Optional.empty();
}
