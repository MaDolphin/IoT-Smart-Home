<#-- (c) https://github.com/MontiCore/monticore -->

${tc.signature("identifier", "className")}
{
Log.debug("MAB0x9007: ${className}.doRun", "${className}");

// check if the initial values are set correctly
Optional
<DTO> contractError = checkContract();
  if (contractError.isPresent()) {
  return contractError.get();
  }

  // get the permitted objects
  return getDomainObjects(securityHelper, daoLib);
  }
