<#-- (c) https://github.com/MontiCore/monticore -->

${tc.signature("identifier", "className")}
{
    Log.info("MAB0x9019: ${className}.doRun: objectId: " + objectId, "${className}");

    if (this.objectId == null) {
    Log.info("0xB9030: given type is null", "${className}");
    return new ErrorDTO("0xB9031", MontiGemErrorFactory.missingField("objectId"));
    }

    Optional<${identifier}> o = daoLib.get${identifier}DAO().findAndLoad(objectId, daoLib, securityHelper.getSessionCompliantResource());

    if (!o.isPresent()) {
        Log.warn("${className} MAB0x9021: Cannot find ${identifier} with objectId " + objectId);
        return new ErrorDTO("MAB0x9021", MontiGemErrorFactory.loadIDError("${identifier}", objectId));
    }

    if (!securityHelper.doesUserHavePermissionType(Permissions.DELETE, o.get().getPermissionClass(), o.get().getPermissionId())) {
      Log.warn("${identifier}_delete MAB0x9011: User doesn't have permission for " +
          o.get().getPermissionClass() + "_" + Permissions.DELETE);
      return new ErrorDTO("MAB0x9011", MontiGemErrorFactory.forbidden());
    }

    try {
        daoLib.get${identifier}DAO().delete(objectId, securityHelper.getSessionCompliantResource());
    } catch (NoSuchElementException e) {
        Log.warn("${className} MAB0x9023: Cannot find ${identifier} with objectId " + objectId);
        return new ErrorDTO("MAB0x9023", MontiGemErrorFactory.loadIDError("${identifier}", objectId));
    }

    Log.debug("MAB0x9013: ${className}.doRun: deleted object with objectId: " + objectId, "${identifier}");
    return new IdDTO(objectId);
}
