<#-- (c) https://github.com/MontiCore/monticore -->

${tc.signature("identifier", "className")}
{
  Log.debug("MAB0x9030: ${className}.doRun: dto: " + dto, "${className}");

  if (this.dto == null) {
    Log.info("0xB9030: given type is null", "${className}");
    return new ErrorDTO("0xB9031", MontiGemErrorFactory.missingField("dto"));
  }

  Optional<${identifier}> o = daoLib.get${identifier}DAO().findAndLoad(dto.getId(), daoLib, securityHelper);

  if (!o.isPresent()) {
    Log.warn(getClass().getName() + " MAB0x9031: Cannot find ${identifier} with objectId " + dto.getId());
    return new ErrorDTO("MAB0x9031", MontiGemErrorFactory.loadIDError("${identifier}", dto.getId()));
  }

    if (!securityHelper.doesUserHavePermissionType(Permissions.UPDATE, o.get().getPermissionClass(), o.get().getPermissionId())) {
      Log.warn("${identifier}_update MAB0x9011: User doesn't have permission for " +
          o.get().getPermissionClass() + "_" + Permissions.UPDATE);
      return new ErrorDTO("MAB0x90011", MontiGemErrorFactory.forbidden());
    }

  ${identifier} object;
  try {
    object = dto.toBuilder(daoLib, securityHelper).build();
  } catch (ValidationException e) {
    Log.warn("${className} MAB0x9033: Object not valid, " + e);
    return new ErrorDTO("MAB0x9033", MontiGemErrorFactory.validationError(e.getMessage()));
  }

  daoLib.get${identifier}DAO().update(object, securityHelper.getSessionCompliantResource());
  Log.debug("MAB0x9034: ${className}.doRun: update of object with id: " + object.getId() + " to " + object, "${className}");
  return new IdDTO(dto.getId());
}
