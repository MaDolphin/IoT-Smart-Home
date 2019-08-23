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

public class DomainUserAktivierungsmailSenden_byStatus extends DomainUserAktivierungsmailSenden_byStatusTOP {
  public DomainUserAktivierungsmailSenden_byStatus(String status) {
    super(status);
  }

  public DTO doRun(SecurityHelper securityHelper, DAOLib daoLib) throws MontiGemError {
    if (!securityHelper.doesUserHavePermission(Permissions.USER_UPDATE, ObjectClasses.USER)) {
      return new ErrorDTO(MontiGemErrorCode.UNAUTHORIZED, " Der Nutzer ist nicht autorisiert für diese Aktion");
    }
    System.out.println(this.status.toUpperCase().replace(" ", "_"));
    DomainUserActivationStatus activationStatus = DomainUserActivationStatus.valueOf(this.status.toUpperCase().replace(" ", "_"));

    List<DomainUser> DomainUsers = daoLib.getDomainUserDAO().getAllUserWithStatus(activationStatus, securityHelper.getSessionCompliantResource());

    for (DomainUser user : DomainUsers) {
      if (user.getId() != securityHelper.getCurrentUser().getId()) {
        try {
          securityHelper.getUserActivationManager().sendActivationEmail(user.getEmail(), user.getUsername(), securityHelper.getSessionCompliantResource(),
                  DatabaseDataSourceUtil.getDatenbankBezeichner(securityHelper.getSessionCompliantResource()));
        } catch (MessagingException e) {
          return new ErrorDTO(MontiGemErrorFactory.mailException());
        }
      }
    }


    return new OkDTO();
  }
}
