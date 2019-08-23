/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be.command.commands;


import de.montigem.be.authz.ObjectClasses;
import de.montigem.be.authz.Permissions;
import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.database.DatabaseDataSourceUtil;
import de.montigem.be.domain.cdmodelhwc.classes.domainuser.DomainUser;
import de.montigem.be.domain.cdmodelhwc.classes.domainuseractivationstatus.DomainUserActivationStatus;
import de.montigem.be.dtos.rte.DTO;
import de.montigem.be.dtos.rte.ErrorDTO;
import de.montigem.be.dtos.rte.OkDTO;
import de.montigem.be.error.MontiGemError;
import de.montigem.be.error.MontiGemErrorCode;
import de.montigem.be.error.MontiGemErrorFactory;
import de.montigem.be.util.DAOLib;

import javax.mail.MessagingException;
import java.util.List;
import java.util.Optional;

public class DomainUserAktivierungsmailSenden_byIds extends DomainUserAktivierungsmailSenden_byIdsTOP {

  public DomainUserAktivierungsmailSenden_byIds(List<Long> userIds) {
    super(userIds);
  }

  public DTO doRun(SecurityHelper securityHelper, DAOLib daoLib) throws MontiGemError {

    if (!securityHelper.doesUserHavePermission(Permissions.USER_UPDATE, ObjectClasses.USER)) {
      return new ErrorDTO(MontiGemErrorCode.UNAUTHORIZED, " Der Nutzer ist nicht autorisiert für diese Aktion");
    }
    for (long id : this.userIds) {
      Optional<DomainUser> user = daoLib.getDomainUserDAO().find(id, securityHelper.getSessionCompliantResource());
      if (!user.isPresent() || user.get().getActivated().equals(DomainUserActivationStatus.AKTIVIERT)) {
        continue;
      }
      try {
        securityHelper.getUserActivationManager().sendActivationEmail(user.get().getEmail(), user.get().getUsername(), securityHelper.getSessionCompliantResource(),
                DatabaseDataSourceUtil.getDatenbankBezeichner(securityHelper.getSessionCompliantResource()));
      } catch (MessagingException e) {
        return new ErrorDTO(MontiGemErrorFactory.mailException());
      }
    }
    return new OkDTO();
  }
}
