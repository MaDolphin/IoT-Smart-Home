/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package de.montigem.be.command.commands;

import de.montigem.be.auth.jwt.MontiGemSecurityUtils;
import de.montigem.be.authz.ObjectClasses;
import de.montigem.be.authz.Roles;
import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.dtos.rte.DTO;
import de.montigem.be.dtos.rte.ErrorDTO;
import de.montigem.be.dtos.rte.IdDTO;
import de.montigem.be.error.MontiGemError;
import de.montigem.be.error.MontiGemErrorFactory;
import de.montigem.be.util.DAOLib;
import de.se_rwth.commons.logging.Log;

public class PasswordResetById extends PasswordResetByIdTOP {

  public DTO doRun(SecurityHelper securityHelper, DAOLib daoLib) {
    if (securityHelper.doesUserHaveRole(Roles.ADMIN, ObjectClasses.USER)) {
      try {
        MontiGemSecurityUtils.setPasswordForUserWithId(userId, password, daoLib.getDomainUserDAO(), securityHelper);
      } catch (MontiGemError MontiGemError) {
        Log.info(MontiGemError.getMessage(), getClass().getName());
        return new ErrorDTO(MontiGemError);
      }
      return new IdDTO(userId);
    }
    else {
      return new ErrorDTO("0xB1552", MontiGemErrorFactory.forbidden("User"));
    }
  }
}
