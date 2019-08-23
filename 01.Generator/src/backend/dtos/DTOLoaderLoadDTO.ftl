<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("originalName")}

{
  List<${originalName}> ${originalName?uncap_first}s = daoLib.get${originalName}DAO().getAll(securityHelper.getSessionCompliantResource())
            .stream()
            .filter(o -> securityHelper.doesUserHavePermissionType(Permissions.READ, o.getPermissionClass(), o.getPermissionId())
            .collect(Collectors.toList());
  if (${originalName?uncap_first}s == null) {
    Log.error("Couldn't get ${originalName?uncap_first}s");
    throw new NoSuchElementException("Couldn't get ${originalName?uncap_first}s");
  }

  ${originalName?uncap_first}s.forEach(b -> dtos.add(new ${originalName}DTO(b)));

}
