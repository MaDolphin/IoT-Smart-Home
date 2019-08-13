<#-- (c) https://github.com/MontiCore/monticore -->
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
    Log.info("MAB0x9019: ${className}.doRun: objectId: " + objectId, "${className}");

    if (this.objectId == null) {
    Log.info("0xB9030: given type is null", "${className}");
    return new ErrorDTO("0xB9031", MontiGemErrorFactory.missingField("objectId"));
    }

    Optional<${identifier}> o = daoLib.get${identifier}DAO().findAndLoad(objectId, daoLib, securityHelper.getSessionCompliantResource());

    if (!o.isPresent()) {
        Log.warn("${className} MAB0x9021: Cannot find ${identifier} with objectId " + objectId);
        return new ErrorDTO("MAB0x9021", MontiGemErrorFactory.loadIDError("${identifier}", objectId));
    }

    if (!securityHelper.doesUserHavePermissionType(Permissions.DELETE, o.get().getPermissionClass(), o.get().getPermissionId())) {
      Log.warn("${identifier}_delete MAB0x9011: User doesn't have permission for " +
          o.get().getPermissionClass() + "_" + Permissions.DELETE);
      return new ErrorDTO("MAB0x9011", MontiGemErrorFactory.forbidden());
    }

    try {
        daoLib.get${identifier}DAO().delete(objectId, securityHelper.getSessionCompliantResource());
    } catch (NoSuchElementException e) {
        Log.warn("${className} MAB0x9023: Cannot find ${identifier} with objectId " + objectId);
        return new ErrorDTO("MAB0x9023", MontiGemErrorFactory.loadIDError("${identifier}", objectId));
    }

    Log.debug("MAB0x9013: ${className}.doRun: deleted object with objectId: " + objectId, "${identifier}");
    return new IdDTO(objectId);
}
