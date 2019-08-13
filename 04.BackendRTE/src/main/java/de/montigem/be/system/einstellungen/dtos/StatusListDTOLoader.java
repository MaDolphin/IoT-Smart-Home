/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be.system.einstellungen.dtos;

import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.domain.cdmodelhwc.classes.domainuseractivationstatus.DomainUserActivationStatus;
import de.montigem.be.util.DAOLib;

import java.util.Arrays;

public class StatusListDTOLoader extends StatusListDTOLoaderTOP {

  public StatusListDTOLoader(DAOLib daoLib, SecurityHelper securityHelper) {
    StatusListDTO dto = new StatusListDTO();
    Arrays.stream(DomainUserActivationStatus.values()).map(StatusListDTOLoader::createRoleListEntryDTO).forEach(r -> dto.getStatus().add(r));
    setDTO(dto);
  }

  public static StatusListEntryDTO createRoleListEntryDTO(DomainUserActivationStatus role) {
    StatusListEntryDTO dto = new StatusListEntryDTO();
    dto.setName(role.getName());
    dto.setId(role.ordinal());
    return dto;
  }

  public StatusListDTOLoader(DAOLib daoLib, long id, SecurityHelper securityHelper) {
    super(daoLib, id, securityHelper);
  }
}
