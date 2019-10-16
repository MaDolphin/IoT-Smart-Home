/* (c) https://github.com/MontiCore/monticore */

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
  private Optional<String> newUsername_rev;

  public DTO doRun(SecurityHelper securityHelper, DAOLib daoLib) {
    try {
      // find object
      DomainUser user = find(securityHelper, daoLib);

      DomainUser currentUser = securityHelper.getCurrentUser();

      // check
      if (!roleCheck(user.getId() != currentUser.getId(), user, securityHelper, daoLib, Roles.ADMIN, ObjectClasses.USER, null)) {
        return new ErrorDTO("0xB1553", MontiGemErrorFactory.forbidden("User"));
      }

      // prepare command to be undoable
      storePreviousValue(user);

      // do action
      doAction(user, securityHelper, daoLib);
    }
    catch (MontiGemError MontiGemError) {
      Log.info(MontiGemError.getMessage(), getClass().getName());
      return new ErrorDTO(MontiGemError);
    }
    return new OkDTO();
  }

  protected void doAction(DomainUser object, SecurityHelper securityHelper, DAOLib daoLib) {
    object.setUsername(newUsername);
    daoLib.getDomainUserDAO().update(object, securityHelper.getSessionCompliantResource());
  }

  protected void undoAction(DomainUser object, SecurityHelper securityHelper, DAOLib daoLib) {
    object.setUsername(newUsername_rev.get());
    daoLib.getDomainUserDAO().update(object, securityHelper.getSessionCompliantResource());
  }

  protected void storePreviousValue(DomainUser object) {
    this.newUsername_rev = Optional.of(object.getUsername());
  }

  protected DomainUser find(SecurityHelper securityHelper, DAOLib daoLib)
      throws MontiGemError {
    DomainUserDAO userDAO = daoLib.getDomainUserDAO();
    Optional<DomainUser> userOptional = userDAO.find(username, securityHelper.getSessionCompliantResource());
    if (!userOptional.isPresent()) {
      throw MontiGemErrorFactory.loadIDError("DomainUser: " + username);
    }

    return userOptional.get();
  }

  protected boolean roleCheck(boolean preCheck, DomainUser object, SecurityHelper securityHelper, DAOLib daoLib, Roles roles, ObjectClasses objectClasses, Long objId) {
    return preCheck && !securityHelper.doesUserHaveRole(roles, objectClasses, objId);
  }

  public DTO undoRun(SecurityHelper securityHelper, DAOLib daoLib) {
    try {
      if (!newUsername_rev.isPresent()) {
        throw MontiGemErrorFactory.undoError("0xB1554", this);
      }

      DomainUser user = find(securityHelper, daoLib);

      DomainUser currentUser = securityHelper.getCurrentUser();

      // check
      if (!roleCheck(user.getId() != currentUser.getId(), user, securityHelper, daoLib, Roles.ADMIN, ObjectClasses.USER, null)) {
        return new ErrorDTO("0xB1553", MontiGemErrorFactory.forbidden("User"));
      }

      // do action
      undoAction(user, securityHelper, daoLib);
    }
    catch (MontiGemError MontiGemError) {
      Log.info(MontiGemError.getMessage(), getClass().getName());
      return new ErrorDTO(MontiGemError);
    }
    return new OkDTO();
  }

  @Override
  public String toString() {
    return "UsernameReset{" +
        "newUsername_rev=" + newUsername_rev +
        ", username='" + username + '\'' +
        ", newUsername='" + newUsername + '\'' +
        ", typeName='" + typeName + '\'' +
        '}';
  }
}
