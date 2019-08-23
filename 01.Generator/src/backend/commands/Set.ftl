<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolTable", "clazzName", "attr", "className", "setterName")}
<#assign typeHelper = tc.instantiate("common.util.TypeHelper")>
<#assign attrName=attr.getName()>
<#assign attrType=attr.printType()>
{
    Log.debug("MAB0x9030: ${className}.doRun: objectId: " + objectId + ", ${attrName}: " + ${attrName}, "${className}");

    if (this.objectId == null) {
      Log.info("0xB9030: given type is null", "${className}");
      return new ErrorDTO("0xB9031", MontiGemErrorFactory.missingField("objectId"));
    }

    if (this.${attrName} == null) {
      Log.info("0xB9030: given type is null", "${className}");
      return new ErrorDTO("0xB9031", MontiGemErrorFactory.missingField("${attrName}"));
    }

    Optional<${clazzName}> o = daoLib.get${clazzName}DAO().findAndLoad(objectId, daoLib, securityHelper.getSessionCompliantResource());

    if (!o.isPresent()) {
        Log.warn(getClass().getName() + " MAB0x9031: Cannot find ${clazzName} with objectId " + objectId);
        return new ErrorDTO("MAB0x9031", MontiGemErrorFactory.loadIDError("${clazzName}", objectId));
    }

    if (!securityHelper.doesUserHavePermissionType(Permissions.UPDATE, o.get().getPermissionClass(), o.get().getPermissionId())) {
      Log.warn("${clazzName}_set MAB0x9011: User doesn't have permission for " +
          o.get().getPermissionClass() + "_" + Permissions.UPDATE);
      return new ErrorDTO("MAB0x90011", MontiGemErrorFactory.forbidden());
    }

    // TODO SVa: move functionality to DAO, so it is possible to load needed data
    o.get().${setterName}(this.${attrName});
    // TODO SVa: rewrite, to not use object twice
    Optional
<String> validationErrors = o.get().getValidator().getValidationErrors(o.get());
    if (validationErrors.isPresent()) {
    Log.warn("${className} MAB0x9033: Object not valid, " + validationErrors.get());
    return new ErrorDTO("MAB0x9033", MontiGemErrorFactory.validationError(validationErrors.get()));
    }

    daoLib.get${clazzName}DAO().${setterName}(objectId, ${attrName});
    Log.debug("MAB0x9034: ${className}.doRun: ${setterName} of object with id: " + objectId + " to " + ${attrName},
    "${className}");
    return new IdDTO(objectId);
    }
