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
${tc.signature("symbolTable", "clazzName", "attr", "className", "setterName")}
<#assign typeHelper = tc.instantiate("common.util.TypeHelper")>
<#assign attrName=attr.getName()>
<#assign attrType=attr.printType()>
{
    Log.debug("MAB0x9030: ${className}.doRun: objectId: " + objectId + ", ${attrName}: " + ${attrName}, "${className}");

    if (this.objectId == null) {
      Log.info("0xB9030: given type is null", "${className}");
      return new ErrorDTO("0xB9031", MontiGemErrorFactory.missingField("objectId"));
    }

    if (this.${attrName} == null) {
      Log.info("0xB9030: given type is null", "${className}");
      return new ErrorDTO("0xB9031", MontiGemErrorFactory.missingField("${attrName}"));
    }

    Optional<${clazzName}> o = daoLib.get${clazzName}DAO().findAndLoad(objectId, daoLib, securityHelper.getSessionCompliantResource());

    if (!o.isPresent()) {
        Log.warn(getClass().getName() + " MAB0x9031: Cannot find ${clazzName} with objectId " + objectId);
        return new ErrorDTO("MAB0x9031", MontiGemErrorFactory.loadIDError("${clazzName}", objectId));
    }

    if (!securityHelper.doesUserHavePermissionType(Permissions.UPDATE, o.get().getPermissionClass(), o.get().getPermissionId())) {
      Log.warn("${clazzName}_set MAB0x9011: User doesn't have permission for " +
          o.get().getPermissionClass() + "_" + Permissions.UPDATE);
      return new ErrorDTO("MAB0x90011", MontiGemErrorFactory.forbidden());
    }

    // TODO SVa: move functionality to DAO, so it is possible to load needed data
    o.get().${setterName}(this.${attrName});
    // TODO SVa: rewrite, to not use object twice
    Optional
<String> validationErrors = o.get().getValidator().getValidationErrors(o.get());
    if (validationErrors.isPresent()) {
    Log.warn("${className} MAB0x9033: Object not valid, " + validationErrors.get());
    return new ErrorDTO("MAB0x9033", MontiGemErrorFactory.validationError(validationErrors.get()));
    }

    daoLib.get${clazzName}DAO().${setterName}(objectId, ${attrName});
    Log.debug("MAB0x9034: ${className}.doRun: ${setterName} of object with id: " + objectId + " to " + ${attrName},
    "${className}");
    return new IdDTO(objectId);
    }
