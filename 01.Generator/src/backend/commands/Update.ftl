<#--
****************************************************************************
MontiCore Language Workbench, www.monticore.de
Copyright (c) 2017, MontiCore, All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its
contributors may be used to endorse or promote products derived from this
software without specific prior written permission.

This software is provided by the copyright holders and contributors
"as is" and any express or implied warranties, including, but not limited
to, the implied warranties of merchantability and fitness for a particular
purpose are disclaimed. In no event shall the copyright holder or
contributors be liable for any direct, indirect, incidental, special,
exemplary, or consequential damages (including, but not limited to,
procurement of substitute goods or services; loss of use, data, or
profits; or business interruption) however caused and on any theory of
liability, whether in contract, strict liability, or tort (including
negligence or otherwise) arising in any way out of the use of this
software, even if advised of the possibility of such damage.
****************************************************************************
-->

${tc.signature("identifier", "className")}
{
  Log.debug("MAB0x9030: ${className}.doRun: dto: " + dto, "${className}");

  if (this.dto == null) {
    Log.info("0xB9030: given type is null", "${className}");
    return new ErrorDTO("0xB9031", MontiGemErrorFactory.missingField("dto"));
  }

  Optional<${identifier}> o = daoLib.get${identifier}DAO().findAndLoad(dto.getId(), daoLib, securityHelper);

  if (!o.isPresent()) {
    Log.warn(getClass().getName() + " MAB0x9031: Cannot find ${identifier} with objectId " + dto.getId());
    return new ErrorDTO("MAB0x9031", MontiGemErrorFactory.loadIDError("${identifier}", dto.getId()));
  }

    if (!securityHelper.doesUserHavePermissionType(Permissions.UPDATE, o.get().getPermissionClass(), o.get().getPermissionId())) {
      Log.warn("${identifier}_update MAB0x9011: User doesn't have permission for " +
          o.get().getPermissionClass() + "_" + Permissions.UPDATE);
      return new ErrorDTO("MAB0x90011", MontiGemErrorFactory.forbidden());
    }

  ${identifier} object;
  try {
    object = dto.toBuilder(daoLib, securityHelper).build();
  } catch (ValidationException e) {
    Log.warn("${className} MAB0x9033: Object not valid, " + e);
    return new ErrorDTO("MAB0x9033", MontiGemErrorFactory.validationError(e.getMessage()));
  }

  daoLib.get${identifier}DAO().update(object, securityHelper.getSessionCompliantResource());
  Log.debug("MAB0x9034: ${className}.doRun: update of object with id: " + object.getId() + " to " + object, "${className}");
  return new IdDTO(dto.getId());
}
