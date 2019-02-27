/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package de.montigem.be.command.commands;

import de.montigem.be.authz.ObjectClasses;
import de.montigem.be.authz.Permissions;
import de.montigem.be.authz.Roles;
import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.domain.cdmodelhwc.classes.domainuser.DomainUser;
import de.montigem.be.domain.cdmodelhwc.classes.roleassignment.RoleAssignment;
import de.montigem.be.domain.cdmodelhwc.classes.roleassignment.RoleAssignmentBuilder;
import de.montigem.be.dtos.rte.DTO;
import de.montigem.be.dtos.rte.ErrorDTO;
import de.montigem.be.dtos.rte.IdDTO;
import de.montigem.be.error.MontiGemError;
import de.montigem.be.error.MontiGemErrorCode;
import de.montigem.be.error.MontiGemErrorFactory;
import de.montigem.be.util.DAOLib;

import javax.xml.bind.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EinstellungenRollenTabelleEntryDTO_create extends EinstellungenRollenTabelleEntryDTO_createTOP {


  public DTO doRun(SecurityHelper securityHelper, DAOLib daoLib) throws MontiGemError {
    String resource = securityHelper.getSessionCompliantResource();
    if (!securityHelper.doesUserHavePermission(Permissions.ROLE_CREATE, ObjectClasses.ROLE_ASSIGNMENT)) {
      return new ErrorDTO(MontiGemErrorCode.FORBIDDEN.getCode(), MontiGemErrorFactory.forbidden(ObjectClasses.USER.getName()));
    }

    Optional<DomainUser> user = daoLib.getDomainUserDAO().find(dto.getUser().getId(), resource);
    if (!user.isPresent()) {
      return new ErrorDTO(MontiGemErrorCode.LOAD_FROM_DB.getCode(), MontiGemErrorFactory.loadIDError("DomainUser", dto.getUser().getId()));
    }
    RoleAssignmentBuilder newRoleAssignment = new RoleAssignmentBuilder();

    if (dto.getObject().getId() >= 0) {
      newRoleAssignment.objId(Optional.of(dto.getObject().getId()));
    }
    newRoleAssignment.user(user.orElse(null));

    Roles role = Roles.values()[(int) dto.getRole().getId()]; // TODO GV, SVa
    if (role == null) {
      return new ErrorDTO(MontiGemErrorCode.NOT_VALID.getCode(), MontiGemErrorFactory.validationError("Role doesn't exist"));
    }
    newRoleAssignment.role(role.getIdentifier());

    List<RoleAssignment> roleAssignments = new ArrayList<>();
    try {
      switch (role) {
        case ADMIN:
          roleAssignments.add(newRoleAssignment.objClass(ObjectClasses.ROLE_ASSIGNMENT.getIdentifier()).build());
          roleAssignments.add(newRoleAssignment.objClass(ObjectClasses.USER.getIdentifier()).build());
          break;
        case LESER:
          roleAssignments.add(newRoleAssignment.objClass(ObjectClasses.USER.getIdentifier()).build()); // TODO GV, SVa
          break;
      }
    } catch (ValidationException e) {
      return new ErrorDTO(MontiGemErrorCode.NOT_VALID.getCode(), MontiGemErrorFactory.validationError(e.getMessage()));
    }

    long id = -1;
    for (RoleAssignment roleAssignment : roleAssignments) {
      RoleAssignment newRole = daoLib.getRoleAssignmentDAO().create(roleAssignment, resource);
      if (newRole == null) {
        return new ErrorDTO(MontiGemErrorCode.STORE_TO_DB.getCode(), MontiGemErrorFactory.storeObjectError(roleAssignment));
      }
      id = newRole.getId();
    }
    if (id >= 0) {
      return new IdDTO(id);
    }
    return new ErrorDTO(MontiGemErrorCode.STORE_TO_DB.getCode(), MontiGemErrorFactory.storeObjectError(roleAssignments));
  }
}
