<#-- (c) https://github.com/MontiCore/monticore -->

${tc.signature("identifier", "className")}
{
    Log.debug("MAB0x9001: ${className}.doRun: dto: " + dto, "${className}");
    if (this.dto == null) {
      Log.info("0xB9030: given type is null", "${className}");
      return new ErrorDTO("0xB9031", MontiGemErrorFactory.missingField("dto"));
    }


${identifier} o;
    try {
      o = dto.toBuilder(daoLib, securityHelper).build();
    } catch (ValidationException e) {
      Log.warn("${className} MAB0x9003: Object not valid", e);
      return new ErrorDTO(MontiGemErrorFactory.validationError("MAB0x9003: " + e.getMessage()));
    }

    if (!securityHelper.doesUserHavePermissionType(Permissions.CREATE, o.getPermissionClass(), o.getPermissionId())) {
      Log.warn("${identifier}_create MAB0x9011: User doesn't have permission for " +
          o.getPermissionClass() + "_" + Permissions.CREATE);
      return new ErrorDTO("MAB0x9011", MontiGemErrorFactory.forbidden());
    }

${identifier} createdO = daoLib.get${identifier}DAO().create(o, securityHelper.getSessionCompliantResource());
    if (securityHelper.grantCurrentUserRoleAndPermission(Roles.ADMIN, createdO.getPermissionClass(), createdO.getId(), null)) {
        Log.debug("MAB0x9013: ${className}.doRun: created object with id: " + createdO.getId(), "${className}");
        return new IdDTO(createdO.getId());
    }
    else
    {
    return new ErrorDTO("MAB0x9012", MontiGemErrorFactory.unknown("Could not grant right to read new Object"));
    }
}
