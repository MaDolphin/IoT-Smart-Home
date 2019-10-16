<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("originalName", "suffix")}

{

  Optional<${originalName}> ${originalName?uncap_first} = daoLib.get${originalName}DAO().findAndLoad(id, daoLib, securityHelper.getSessionCompliantResource());
  if (${originalName?uncap_first} == null || !${originalName?uncap_first}.isPresent() || !securityHelper.doesUserHavePermissionType(Permissions.READ, ${originalName?uncap_first}.get().getPermissionClass(), ${originalName?uncap_first}.get().getPermissionId())) {
    Log.error("Couldn't access the ${originalName?uncap_first} with id: " + id);
    throw new NoSuchElementException("Auf die angegebene ${originalName} kann nicht zugegriffen werden.");
  }
  return new ${originalName}${suffix}DTO(${originalName?uncap_first}.get());

}
