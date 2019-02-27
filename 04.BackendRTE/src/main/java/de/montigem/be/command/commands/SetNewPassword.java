/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package de.montigem.be.command.commands;

import de.montigem.be.auth.jwt.MontiGemSecurityUtils;
import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.domain.cdmodelhwc.classes.domainuser.DomainUser;
import de.montigem.be.dtos.rte.DTO;
import de.montigem.be.dtos.rte.ErrorDTO;
import de.montigem.be.dtos.rte.OkDTO;
import de.montigem.be.error.MontiGemError;
import de.montigem.be.error.MontiGemErrorCode;
import de.montigem.be.util.DAOLib;
import org.apache.shiro.codec.Base64;

import javax.ws.rs.core.Response;

public class SetNewPassword extends SetNewPasswordTOP {

  public SetNewPassword() {
    super();
  }

  public SetNewPassword(String oldPassword, String newPassword) {
    super(oldPassword, newPassword);
  }

  public DTO doRun(SecurityHelper securityHelper, DAOLib daoLib) throws MontiGemError {
    DomainUser user = securityHelper.getCurrentUser();
    String oldPwd = MontiGemSecurityUtils.encodePassword(this.oldPassword, Base64.decode(user.getPasswordSaltBase64()));
    if (oldPwd.equals(user.getEncodedPassword().orElse(""))) {
      MontiGemSecurityUtils.setPasswordForUser(user, this.newPassword, daoLib.getDomainUserDAO(), securityHelper);
      return new OkDTO();
    }
    return new ErrorDTO(new MontiGemError(MontiGemErrorCode.NOT_VALID, Response.Status.NOT_ACCEPTABLE, "Das angegebene Passwort war falsch"));
  }
}

