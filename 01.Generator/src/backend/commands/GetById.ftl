<#-- (c) https://github.com/MontiCore/monticore -->

${tc.signature("identifier", "className")}
{
    Log.debug("MAB0x9020: ${className}.doRun: objectId: " + objectId, "${className}");

    if (this.objectId == null) {
      Log.info("0xB9030: given type is null", "${className}");
      return new ErrorDTO("0xB9031", MontiGemErrorFactory.missingField("objectId"));
    }

    Optional<${identifier}> o = daoLib.get${identifier}DAO().findAndLoad(objectId, daoLib, securityHelper.getSessionCompliantResource());

    if (!o.isPresent()) {
        Log.warn("${identifier} MAB0x9021: Cannot find ${className} with id " + objectId);
        return new ErrorDTO("MAB0x9021", MontiGemErrorFactory.loadIDError("${identifier}", objectId));
    }

    if (!securityHelper.doesUserHavePermissionType(Permissions.READ, o.get().getPermissionClass(), o.get().getPermissionId())) {
      Log.warn("${identifier}_getById MAB0x9011: User doesn't have permission for " +
          o.get().getPermissionClass() + "_" + Permissions.READ);
      return new ErrorDTO("MAB0x90011", MontiGemErrorFactory.forbidden());
    }

    Log.debug("MAB0x9013: ${className}.doRun: get object with id: " + objectId, "${className}");
    return new ${identifier}FullDTOLoader(daoLib, objectId, securityHelper).getDTO();
}
