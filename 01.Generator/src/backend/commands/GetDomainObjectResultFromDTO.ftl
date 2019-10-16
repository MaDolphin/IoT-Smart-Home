<#-- (c) https://github.com/MontiCore/monticore -->

${tc.signature("className", "varName")}
{
try {
return Result.ok(${varName}.toBuilder(daoLib, securityHelper).build());
}
catch (ValidationException e) {
Log.warn("${className} MAB0x9020: Object not valid", e);
return Result.err(new ErrorDTO(MontiGemErrorFactory.validationError("MAB0x9020: " + e.getMessage())));
}
}
