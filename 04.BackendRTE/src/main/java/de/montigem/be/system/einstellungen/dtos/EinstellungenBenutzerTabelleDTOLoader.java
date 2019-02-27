/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package de.montigem.be.system.einstellungen.dtos;

import de.montigem.be.authz.ObjectClasses;
import de.montigem.be.authz.Permissions;
import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.domain.cdmodelhwc.classes.domainuser.DomainUser;
import de.montigem.be.util.DAOLib;

import java.util.List;

public class EinstellungenBenutzerTabelleDTOLoader extends EinstellungenBenutzerTabelleDTOLoaderTOP {

  public EinstellungenBenutzerTabelleDTOLoader(DAOLib daoLib, SecurityHelper securityHelper) {
    EinstellungenBenutzerTabelleDTO dto = new EinstellungenBenutzerTabelleDTO();
    List<DomainUser> users = daoLib.getDomainUserDAO().loadAllPermitted(daoLib, securityHelper, ObjectClasses.USER, Permissions.USER_READ);
    users.stream().map(EinstellungenBenutzerTabelleDTOLoader::createEinstellungenBenutzerTabelleEntryDTO).forEach(u -> dto.getAlleBenutzer().add(u));
    setDTO(dto);
  }

  public EinstellungenBenutzerTabelleDTOLoader(DAOLib daoLib, long id, SecurityHelper securityHelper) {
    super(daoLib, id, securityHelper);
  }

  public static EinstellungenBenutzerTabelleEntryDTO createEinstellungenBenutzerTabelleEntryDTO(DomainUser user) {
    EinstellungenBenutzerTabelleEntryDTO dto = new EinstellungenBenutzerTabelleEntryDTO();
    dto.setEmail(user.getEmail());
    dto.setInitials(user.getInitials().orElse(""));
    dto.setAktivierungsstatus(user.getActivated().getName());
    dto.setTAktiv(user.isEnabled());
    dto.setUsername(user.getUsername());
    dto.setRegistrationDate(user.getRegistrationDate());
    dto.setId(user.getId());
    return dto;
  }

}
