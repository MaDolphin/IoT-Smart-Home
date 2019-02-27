/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package de.montigem.be.system.einstellungen.dtos;

import de.montigem.be.authz.Roles;
import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.util.DAOLib;

import java.util.Arrays;

public class RoleListDTOLoader extends RoleListDTOLoaderTOP {
  
  public RoleListDTOLoader(DAOLib daoLib, SecurityHelper securityHelper) {
    RoleListDTO dto = new RoleListDTO();
    Arrays.stream(Roles.values()).map(RoleListDTOLoader::createRoleListEntryDTO).forEach(r -> dto.getRoles().add(r));
    setDTO(dto);
  }

  public static RoleListEntryDTO createRoleListEntryDTO(Roles role) {
    RoleListEntryDTO dto = new RoleListEntryDTO();
    dto.setName(role.getName());
    dto.setId(role.ordinal());
    return dto;
  }
  
  public RoleListDTOLoader(DAOLib daoLib, long id, SecurityHelper securityHelper)
  
  {
    super(daoLib, id, securityHelper);
  }
  
}
