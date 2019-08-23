<#-- (c) https://github.com/MontiCore/monticore -->

${tc.signature("identifier", "className")}
{
    Log.info("MAB0x9020: ${className}.doRun: objectId: " + objectId, "${className}");
    return new ${identifier}DTOLoader(daoLib, objectId, securityHelper).getDTO();
}
