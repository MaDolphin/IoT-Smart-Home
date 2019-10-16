<#-- (c) https://github.com/MontiCore/monticore -->

${tc.signature("identifier", "className")}
{
    Log.debug("MAB0x9001: ${className}.doRun: dto: " + dto, "${className}");

// check if the initial values are set correctly
Optional
<DTO> contractError = checkContract();
  if (contractError.isPresent()) {
  return contractError.get();
  }

  // get the object
  Result<${identifier}, ErrorDTO> object = getDomainObject(securityHelper, daoLib);
  if (object.isErr()) {
  return object.getErr();
  }

  // permission check
  Optional
  <ErrorDTO> permissionError = checkPermission(object.get(), securityHelper, daoLib);
    if (permissionError.isPresent()) {
    return permissionError.get();
    }

    return doAction(object.get(), securityHelper, daoLib);
    }