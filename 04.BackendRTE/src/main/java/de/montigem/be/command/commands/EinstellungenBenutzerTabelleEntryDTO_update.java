/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be.command.commands;

import de.montigem.be.authz.ObjectClasses;
import de.montigem.be.authz.Permissions;
import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.domain.cdmodelhwc.classes.domainuser.DomainUser;
import de.montigem.be.dtos.rte.DTO;
import de.montigem.be.dtos.rte.ErrorDTO;
import de.montigem.be.dtos.rte.IdDTO;
import de.montigem.be.error.MontiGemError;
import de.montigem.be.error.MontiGemErrorCode;
import de.montigem.be.error.MontiGemErrorFactory;
import de.montigem.be.system.einstellungen.dtos.EinstellungenBenutzerTabelleEntryDTO;
import de.montigem.be.util.DAOLib;

import java.util.Optional;

public class EinstellungenBenutzerTabelleEntryDTO_update extends EinstellungenBenutzerTabelleEntryDTO_updateTOP {
  
  public DTO doRun(SecurityHelper securityHelper, DAOLib daoLib) throws MontiGemError {
    if(!securityHelper.doesUserHavePermission(Permissions.USER_UPDATE, ObjectClasses.USER)){
      return new ErrorDTO(MontiGemErrorCode.FORBIDDEN.getCode(), MontiGemErrorFactory.forbidden(ObjectClasses.USER.getName()));
    }

    EinstellungenBenutzerTabelleEntryDTO dto = getDto();
    Optional<DomainUser> oldUserOpt = daoLib.getDomainUserDAO().find(dto.getId(), securityHelper.getSessionCompliantResource());
    if(!oldUserOpt.isPresent()) {
      return new ErrorDTO(MontiGemErrorCode.LOAD_FROM_DB.getCode(), MontiGemErrorFactory.loadIDError("DomainUser", dto.getId()));
    }
    DomainUser oldUser = oldUserOpt.get();
    if(!dto.getUsername().equals(oldUser.getUsername())) {
      if(daoLib.getDomainUserDAO().noUserExistWithUserName(dto.getUsername(), securityHelper.getSessionCompliantResource())) {
        oldUser.setUsername(dto.getUsername());
      } else {
        return new ErrorDTO(MontiGemErrorCode.NON_UNIQUE_NAME.getCode(), MontiGemErrorFactory.nameIsNotUnique(dto.getUsername()));
      }
    }

    if(!dto.getEmail().equals(oldUser.getEmail())) {
      if(daoLib.getDomainUserDAO().noUserExistWithEmail(dto.getEmail(), securityHelper.getSessionCompliantResource())) {
        oldUser.setEmail(dto.getEmail());
      } else {
        return new ErrorDTO(MontiGemErrorCode.NON_UNIQUE_NAME.getCode(), MontiGemErrorFactory.nameIsNotUnique(dto.getEmail()));
      }
    }

    if(!dto.getInitials().isEmpty() && !dto.getInitials().equals(oldUser.getInitials().orElse(""))) {
      if(daoLib.getDomainUserDAO().noUserExistWithInitials(dto.getInitials(), securityHelper.getSessionCompliantResource())) {
        oldUser.setInitials(Optional.of(dto.getInitials()));
      } else {
        return new ErrorDTO(MontiGemErrorCode.NON_UNIQUE_NAME.getCode(), MontiGemErrorFactory.nameIsNotUnique(dto.getInitials()));
      }
    }

    oldUser.setEnabled(dto.isTAktiv());
    Optional<DomainUser> newUser = daoLib.getDomainUserDAO().updateWithoutAssociations(oldUser, securityHelper.getSessionCompliantResource());
    if(newUser.isPresent()) {
      return new IdDTO(newUser.get().getId());
    }
    
    return new ErrorDTO(MontiGemErrorCode.STORE_TO_DB.getCode(), MontiGemErrorFactory.storeObjectError(oldUser));
  }
  
}
