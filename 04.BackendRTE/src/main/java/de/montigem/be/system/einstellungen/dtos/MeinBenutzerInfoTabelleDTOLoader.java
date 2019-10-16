/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be.system.einstellungen.dtos;

import de.montigem.be.authz.ObjectClasses;
import de.montigem.be.authz.Permissions;
import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.domain.cdmodelhwc.classes.domainuser.DomainUser;
import de.montigem.be.util.DAOLib;

import java.util.List;
import java.util.stream.Collectors;

public class MeinBenutzerInfoTabelleDTOLoader extends MeinBenutzerInfoTabelleDTOLoaderTOP {

  public MeinBenutzerInfoTabelleDTOLoader(DAOLib daoLib, SecurityHelper securityHelper) {
    DomainUser user = securityHelper.getCurrentUser();

    MeinBenutzerInfoTabelleDTO dto = new MeinBenutzerInfoTabelleDTO();
    List<DomainUser> users = daoLib.getDomainUserDAO().getAll(securityHelper.getSessionCompliantResource());
    dto.setExistingUsernames(users.stream().map(u -> u.getUsername()).collect(Collectors.toList()));
    dto.setEmail(user.getEmail());
    dto.setInitials(user.getInitials().orElse("N.N."));
    dto.setUsername(user.getUsername());
    dto.setRegistrationDate(user.getRegistrationDate());
    setDTO(dto);
  }

  public MeinBenutzerInfoTabelleDTOLoader(DAOLib daoLib, long id, SecurityHelper securityHelper) {
    DomainUser user = securityHelper.getCurrentUser();

    if (securityHelper.doesUserHavePermission(Permissions.USER_READ, ObjectClasses.USER, id)) {

      //System.out.println("Benutzer" + user.getId());
      MeinBenutzerInfoTabelleDTO dto = new MeinBenutzerInfoTabelleDTO();
      List<DomainUser> users = daoLib.getDomainUserDAO().getAll(securityHelper.getSessionCompliantResource());
      dto.setExistingUsernames(users.stream().map(u -> u.getUsername()).collect(Collectors.toList()));
      dto.setEmail(user.getEmail());
      dto.setInitials(user.getInitials().orElse(""));
      dto.setUsername(user.getUsername());
      dto.setRegistrationDate(user.getRegistrationDate());
      setDTO(dto);

    }
  }
}
