<#-- (c) https://github.com/MontiCore/monticore -->

${tc.signature("identifier", "className", "attribute", "setterName")}
{
// TODO SVa: move functionality to DAO, so it is possible to load needed data
object.${setterName}(this.${attribute});

// TODO SVa: rewrite, to not use object twice
Optional
<String> validationErrors = object.getValidator().getValidationErrors(object);
  if (validationErrors.isPresent()) {
  Log.warn("${className} MAB0x9043: Object not valid, " + validationErrors.get());
  return new ErrorDTO("MAB0x9043", MontiGemErrorFactory.validationError(validationErrors.get()));
  }

  daoLib.get${identifier}DAO().${setterName}(objectId, ${attribute});
  Log.debug("MAB0x9044: ${className}.doAction: set${setterName} of object with id: " + objectId + " to " + ${attribute}, "${className}");
  return new IdDTO(objectId);
  }