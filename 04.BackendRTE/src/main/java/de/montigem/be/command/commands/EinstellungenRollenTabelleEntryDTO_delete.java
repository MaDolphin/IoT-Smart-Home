/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package de.montigem.be.command.commands;

import de.montigem.be.authz.ObjectClasses;
import de.montigem.be.authz.Permissions;
import de.montigem.be.authz.Roles;
import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.domain.cdmodelhwc.classes.roleassignment.RoleAssignment;
import de.montigem.be.dtos.rte.DTO;
import de.montigem.be.dtos.rte.ErrorDTO;
import de.montigem.be.dtos.rte.OkDTO;
import de.montigem.be.error.MontiGemError;
import de.montigem.be.error.MontiGemErrorCode;
import de.montigem.be.error.MontiGemErrorFactory;
import de.montigem.be.util.DAOLib;

import java.util.Optional;

public class EinstellungenRollenTabelleEntryDTO_delete extends EinstellungenRollenTabelleEntryDTO_deleteTOP {

  public DTO doRun(SecurityHelper securityHelper, DAOLib daoLib) throws MontiGemError  {
    String resource = securityHelper.getSessionCompliantResource();
    if (!securityHelper.doesUserHavePermission(Permissions.ROLE_DELETE, ObjectClasses.ROLE_ASSIGNMENT)) {
      return new ErrorDTO(MontiGemErrorCode.FORBIDDEN.getCode(), MontiGemErrorFactory.forbidden(ObjectClasses.USER.getName()));
    }

    Optional<RoleAssignment> roleAssignmentOpt = daoLib.getRoleAssignmentDAO().find(id, resource);
    if (!roleAssignmentOpt.isPresent()) {
      return new ErrorDTO(MontiGemErrorCode.LOAD_FROM_DB.getCode(), MontiGemErrorFactory.loadIDError("RoleAssignment", id));
    }
    RoleAssignment roleAssignment = roleAssignmentOpt.get();

    if (securityHelper.getCurrentUser().getId() == roleAssignment.getUser().getId() && Roles.roles(roleAssignment.getRole()).orElse(null) == Roles.ADMIN) {
      return new ErrorDTO(MontiGemErrorCode.FORBIDDEN.getCode(), MontiGemErrorFactory.forbiddenSelf());
    }
    daoLib.getRoleAssignmentDAO().delete(id, resource);

    roleAssignmentOpt = daoLib.getRoleAssignmentDAO().find(id, resource);
    if (!roleAssignmentOpt.isPresent()) {
      return new OkDTO();
    }
    return new ErrorDTO(MontiGemErrorCode.STORE_TO_DB.getCode(), MontiGemErrorFactory.storeObjectError(roleAssignment));
  }

}
