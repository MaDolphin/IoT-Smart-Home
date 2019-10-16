<#-- (c) https://github.com/MontiCore/monticore -->

${tc.signature("identifier", "className")}
{
${identifier} object;
try {
object = dto.toBuilder(daoLib, securityHelper).build();
} catch (ValidationException e) {
Log.warn("${className} MAB0x9047: Object not valid, " + e);
return new ErrorDTO("MAB0x9047", MontiGemErrorFactory.validationError(e.getMessage()));
}

daoLib.get${identifier}DAO().update(object, securityHelper.getSessionCompliantResource());
Log.debug("MAB0x9048: ${className}.doRun: update of object with id: " + object.getId() + " to " + object, "${className}");
return new IdDTO(dto.getId());
}