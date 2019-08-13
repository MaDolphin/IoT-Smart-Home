/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be.system.einstellungen.dtos;

import de.montigem.be.authz.ObjectClasses;
import de.montigem.be.authz.Permissions;
import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.domain.cdmodelhwc.classes.domainuser.DomainUser;
import de.montigem.be.util.DAOLib;

import java.util.List;
import java.util.stream.Collectors;

public class UserListDTOLoader extends UserListDTOLoaderTOP {

  public UserListDTOLoader(DAOLib daoLib, SecurityHelper securityHelper) {
    UserListDTO dto = new UserListDTO();
    if (securityHelper.doesUserHavePermission(Permissions.USER_READ, ObjectClasses.USER)) {
      List<DomainUser> users = daoLib.getDomainUserDAO().getAll(securityHelper.getSessionCompliantResource());
      dto.setUsers(users.stream().map(UserListDTOLoader::createUserListEntryDTO).collect(Collectors.toList()));
    }
    setDTO(dto);
  }

  public static UserListEntryDTO createUserListEntryDTO(DomainUser DomainUser) {
    UserListEntryDTO dto = new UserListEntryDTO();
    dto.setUsername(DomainUser.getUsername());
    dto.setId(DomainUser.getId());
    return dto;
  }

  public UserListDTOLoader(DAOLib daoLib, long id, SecurityHelper securityHelper) {
    super(daoLib, id, securityHelper);
  }

}
