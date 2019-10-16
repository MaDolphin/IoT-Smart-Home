<#-- (c) https://github.com/MontiCore/monticore -->

${tc.signature("identifier", "className", "varName")}
{
Optional<${identifier}> object = daoLib.get${identifier}DAO().findAndLoad(${varName}, daoLib, securityHelper.getSessionCompliantResource());
if (!object.isPresent()) {
Log.warn("${className} MAB0x9021: Cannot find ${identifier} with objectId " + ${varName});
return Result.err(new ErrorDTO("MAB0x9021", MontiGemErrorFactory.loadIDError("${identifier}", ${varName})));
}

return Result.ok(object);
}
