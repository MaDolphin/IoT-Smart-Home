/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package de.montigem.be.system.einstellungen.dtos;

import de.montigem.be.authz.ObjectClasses;
import de.montigem.be.authz.Permissions;
import de.montigem.be.authz.Roles;
import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.domain.cdmodelhwc.classes.roleassignment.RoleAssignment;
import de.montigem.be.util.DAOLib;

import java.util.List;

public class EinstellungenRollenTabelleDTOLoader extends EinstellungenRollenTabelleDTOLoaderTOP {

  public EinstellungenRollenTabelleDTOLoader(DAOLib daoLib, SecurityHelper securityHelper) {
    EinstellungenRollenTabelleDTO dto = new EinstellungenRollenTabelleDTO();
    if (securityHelper.doesUserHavePermission(Permissions.USER_READ, ObjectClasses.USER)) {
      List<RoleAssignment> roles = daoLib.getRoleAssignmentDAO().getAll(securityHelper.getSessionCompliantResource());
      roles.forEach((role) -> {
        EinstellungenRollenTabelleEntryDTO entryDTO = new EinstellungenRollenTabelleEntryDTO();
        Roles.roles(role.getRole()).ifPresent(r -> entryDTO.setRole(RoleListDTOLoader.createRoleListEntryDTO(r)));
        entryDTO.setUser(UserListDTOLoader.createUserListEntryDTO(role.getUser()));
        entryDTO.setId(role.getId());
        entryDTO.setObject(new ObjectListEntryDTO(-1, ""));
        ObjectClasses.objectClasses(role.getObjClass()).ifPresent(o -> entryDTO.setObjectClass(o.getName()));
        dto.getUsers().add(entryDTO);
      });
    }
    setDTO(dto);
  }

  public EinstellungenRollenTabelleDTOLoader(DAOLib daoLib, long id, SecurityHelper securityHelper) {
    super(daoLib, id, securityHelper);
  }

}
