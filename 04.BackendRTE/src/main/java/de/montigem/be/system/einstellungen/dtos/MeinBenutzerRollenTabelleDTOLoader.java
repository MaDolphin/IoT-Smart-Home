/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be.system.einstellungen.dtos;

import de.montigem.be.authz.ObjectClasses;
import de.montigem.be.authz.Roles;
import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.domain.cdmodelhwc.classes.roleassignment.RoleAssignment;
import de.montigem.be.util.DAOLib;

import java.util.Collection;

public class MeinBenutzerRollenTabelleDTOLoader extends MeinBenutzerRollenTabelleDTOLoaderTOP {


  public MeinBenutzerRollenTabelleDTOLoader(DAOLib daoLib, SecurityHelper securityHelper) {
    MeinBenutzerRollenTabelleDTO dto = new MeinBenutzerRollenTabelleDTO();
    Collection<RoleAssignment> roles = daoLib.getRoleAssignmentDAO().getRoleAssignments(securityHelper.getCurrentUser().getId());
    roles.forEach((role) -> {
      MeinBenutzerRollenTabelleEntryDTO entryDTO = new MeinBenutzerRollenTabelleEntryDTO();
      Roles.roles(role.getRole()).ifPresent(r -> entryDTO.setRole(r.getName()));
      entryDTO.setId(role.getId());
      ObjectClasses.objectClasses(role.getObjClass()).ifPresent(o -> entryDTO.setName(o.getName()));
      dto.getUsers().add(entryDTO);
    });
    setDTO(dto);
  }

  public MeinBenutzerRollenTabelleDTOLoader(DAOLib daoLib, long id, SecurityHelper securityHelper) {
    super(daoLib, id, securityHelper);
  }

}
