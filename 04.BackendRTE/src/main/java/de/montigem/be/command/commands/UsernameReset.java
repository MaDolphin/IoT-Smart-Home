/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package de.montigem.be.command.commands;

import de.montigem.be.authz.ObjectClasses;
import de.montigem.be.authz.Roles;
import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.domain.cdmodelhwc.classes.domainuser.DomainUser;
import de.montigem.be.domain.cdmodelhwc.daos.DomainUserDAO;
import de.montigem.be.dtos.rte.DTO;
import de.montigem.be.dtos.rte.ErrorDTO;
import de.montigem.be.dtos.rte.OkDTO;
import de.montigem.be.error.MontiGemError;
import de.montigem.be.error.MontiGemErrorFactory;
import de.montigem.be.util.DAOLib;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class UsernameReset extends UsernameResetTOP {

  public DTO doRun(SecurityHelper securityHelper, DAOLib daoLib) {
    try {
      DomainUserDAO userDAO = daoLib.getDomainUserDAO();
      Optional<DomainUser> userOptional = userDAO.find(username, securityHelper.getSessionCompliantResource());
      if (!userOptional.isPresent()) {
        throw MontiGemErrorFactory.loadIDError("DomainUser: " + username);
      }
      DomainUser user = userOptional.get();
      DomainUser currentUser = securityHelper.getCurrentUser();
      if (!securityHelper.doesUserHaveRole(Roles.ADMIN, ObjectClasses.USER) && user.getId() != currentUser.getId()) {
        return new ErrorDTO("0xB1553", MontiGemErrorFactory.forbidden("User"));
      }
      user.setUsername(newUsername);
      userDAO.update(user, securityHelper.getSessionCompliantResource());
    } catch (MontiGemError MontiGemError) {
      Log.info(MontiGemError.getMessage(), getClass().getName());
      return new ErrorDTO(MontiGemError);
    }
    return new OkDTO();
  }
}