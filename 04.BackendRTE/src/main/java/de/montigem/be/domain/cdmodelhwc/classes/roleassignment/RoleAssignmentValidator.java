package de.montigem.be.domain.cdmodelhwc.classes.roleassignment;

import de.montigem.be.authz.ObjectClasses;
import de.montigem.be.authz.Roles;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class RoleAssignmentValidator extends RoleAssignmentValidatorTOP {

  public RoleAssignmentValidator() {
  }

  @Override
  // TODO KA: Fehlemeldungen
  public Optional<String> getValidationErrors(RoleAssignment roleAssignmnt) {
    Optional<String> message = super.getValidationErrors(roleAssignmnt);
    if (message.isPresent()) {
      return message;
    }
    message = isRoleObjectidValid(roleAssignmnt);
    if (message.isPresent()) {
      return message;
    }
    // if role is not admin then it may only be assigned to accounts
    boolean isValid = !roleAssignmnt.getObjClass().equals("") ?
            isObjClassHasValue(roleAssignmnt) && roleAssignmnt.getRole().equals(Roles.ADMIN.getIdentifier())
            : roleAssignmnt.getRole().equals(Roles.ADMIN.getIdentifier());
    if (!isValid) {
      String msg = "Wenn Rolle ungleich admin ist, dann kann es nur Konten zugewiesen werden.\n";
      Log.debug(msg, getClass().getName());
      return Optional.of(msg);
    }
    return message;
  }

  public Optional<String> isRoleObjectidValid(RoleAssignment a) {
    if (!Roles.roles(a.getRole()).isPresent()) {
      return Optional.of("Rolle ist ungültig");
    }
    Roles role = Roles.roles(a.getRole()).get();
    switch (role) {
      case ADMIN:
        if (a.getObjId().isPresent()) {
          return Optional.of("Rolle darf kein Konto haben");
        }
        break;
      case LESER:
        if (!a.getObjId().isPresent()) {
          return Optional.of("Rolle muss ein Konto haben");
        }
        break;
    }
    return Optional.empty();
  }

  @Override
  public Optional<String> isRoleValid(RoleAssignment a) {
    Optional<String> message = super.isRoleValid(a);
    if (message.isPresent()) {
      return message;
    }
    String role = a.getRole();
    boolean isValid =
            role.equals(Roles.LESER.getIdentifier()) ||
                    role.equals(Roles.ADMIN.getIdentifier());
    if (!isValid) {
      String msg = " Rolle " + role + " ist nicht zulaessig:\n";
      Log.debug(msg, getClass().getName());
      return Optional.of(msg);
    }

    return Optional.empty();
  }

  private boolean isObjClassHasValue(RoleAssignment a) {
    String objClass = a.getObjClass();
    return objClass.equals(ObjectClasses.ROLE_ASSIGNMENT.getIdentifier()) ||
            objClass.equals(ObjectClasses.USER.getIdentifier());
  }

}
