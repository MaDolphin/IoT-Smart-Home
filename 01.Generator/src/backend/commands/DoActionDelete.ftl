<#-- (c) https://github.com/MontiCore/monticore -->

${tc.signature("identifier", "className")}
{
try {
daoLib.get${identifier}DAO().delete(objectId, securityHelper.getSessionCompliantResource());
}
catch (NoSuchElementException e) {
Log.warn("${className} MAB0x9047: Cannot find DomainUser with objectId " + objectId);
return new ErrorDTO("MAB0x9047", MontiGemErrorFactory.loadIDError("${identifier}", objectId));
}

Log.debug("MAB0x9048: ${className}.doAction: deleted object with objectId: " + objectId, "${className}");
return new IdDTO(objectId);
}