/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be.command.commands;

import de.montigem.be.authz.ObjectClasses;
import de.montigem.be.authz.Permissions;
import de.montigem.be.authz.Roles;
import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.domain.cdmodelhwc.classes.domainuser.DomainUser;
import de.montigem.be.domain.cdmodelhwc.classes.roleassignment.RoleAssignment;
import de.montigem.be.dtos.rte.DTO;
import de.montigem.be.dtos.rte.ErrorDTO;
import de.montigem.be.dtos.rte.IdDTO;
import de.montigem.be.error.MontiGemError;
import de.montigem.be.error.MontiGemErrorCode;
import de.montigem.be.error.MontiGemErrorFactory;
import de.montigem.be.util.DAOLib;

import java.util.Optional;

public class EinstellungenRollenTabelleEntryDTO_update extends EinstellungenRollenTabelleEntryDTO_updateTOP {

  public DTO doRun(SecurityHelper securityHelper, DAOLib daoLib) throws MontiGemError  {
    String resource = securityHelper.getSessionCompliantResource();
    if (!securityHelper.doesUserHavePermission(Permissions.ROLE_UPDATE, ObjectClasses.ROLE_ASSIGNMENT)) {
      return new ErrorDTO(MontiGemErrorCode.FORBIDDEN.getCode(), MontiGemErrorFactory.forbidden(ObjectClasses.USER.getName()));
    }

    Optional<RoleAssignment> roleAssignmentOpt = daoLib.getRoleAssignmentDAO().find(dto.getId(), resource);
    if (!roleAssignmentOpt.isPresent()) {
      return new ErrorDTO(MontiGemErrorCode.LOAD_FROM_DB.getCode(), MontiGemErrorFactory.loadIDError("RoleAssignment", dto.getId()));
    }
    RoleAssignment roleAssignment = roleAssignmentOpt.get();
    if (securityHelper.getCurrentUser().getId() == roleAssignment.getUser().getId() && Roles.roles(roleAssignment.getRole()).orElse(null) == Roles.ADMIN) {
      return new ErrorDTO(MontiGemErrorCode.FORBIDDEN.getCode(), MontiGemErrorFactory.forbiddenSelf());
    }

    Optional<DomainUser> user = daoLib.getDomainUserDAO().find(dto.getUser().getId(), resource);
    if (!user.isPresent()) {
      return new ErrorDTO(MontiGemErrorCode.LOAD_FROM_DB.getCode(), MontiGemErrorFactory.loadIDError("DomainUser", dto.getUser().getId()));
    }

    roleAssignment.setObjId(dto.getObject().getId() >= 0 ? Optional.of(dto.getObject().getId()) : Optional.empty());
    roleAssignment.setUser(user.get());
    roleAssignment.setRole(Roles.values()[(int) dto.getRole().getId()].getIdentifier());
    Optional<RoleAssignment> newRoleAssignment = daoLib.getRoleAssignmentDAO().update(roleAssignment, resource);

    if(newRoleAssignment.isPresent()) {
      return new IdDTO(newRoleAssignment.get().getId());
    }

    return new ErrorDTO(MontiGemErrorCode.STORE_TO_DB.getCode(), MontiGemErrorFactory.storeObjectError(roleAssignment));
  }

}
