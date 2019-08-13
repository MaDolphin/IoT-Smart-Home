/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be.authz.util;

import de.montigem.be.auth.UserActivationManager;
import de.montigem.be.auth.jwt.PrincipalWrapper;
import de.montigem.be.authz.ObjectClasses;
import de.montigem.be.authz.Roles;
import de.montigem.be.authz.model.Role;
import de.montigem.be.domain.cdmodelhwc.classes.domainuser.DomainUser;
import de.montigem.be.domain.cdmodelhwc.classes.roleassignment.RoleAssignment;
import de.montigem.be.domain.cdmodelhwc.daos.DomainUserDAO;
import de.montigem.be.domain.cdmodelhwc.daos.RoleAssignmentDAO;
import de.montigem.be.util.DAOLib;
import de.se_rwth.commons.logging.Log;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Stateless
public class SecurityHelper {

  @Inject
  private RoleAssignmentDAO raDAO;

  @Inject
  private DomainUserDAO userDAO;

  @Inject
  private RolePermissionManager rolePermissionManager;

  @Inject
  private RoleAssignmentDAO roleAssignmentDAO;

  @Inject
  private UserActivationManager userActivationManager;

  private long currentUserID;

  public boolean doesUserHavePermssionForAccount(String permission, long userId, long accountId) {
    // First get all role assignments for user on accounts
    Collection<RoleAssignment> roleAssignments = roleAssignmentDAO
            .getRoleAssignments(userId, ObjectClasses.USER.getIdentifier());

    // Look for specific account
    for (RoleAssignment roleAssignment : roleAssignments) {
      // Important: null here means that the role applies to all accounts
      if (!roleAssignment.getObjId().isPresent() || accountId == roleAssignment.getObjId().get().longValue()) {
        // Get actual role for role assignment
        if (rolePermissionManager.getRole(roleAssignment.getRole()).isPresent()) {
          Role role = rolePermissionManager.getRole(roleAssignment.getRole()).get();
          // Check permissions of role
          return role.getPermissions().contains(permission);
        }
      }
    }

    return false;
  }

  public DomainUser getCurrentUser() {
    return SecurityUtils.getSubject().getPrincipals().oneByType(PrincipalWrapper.class).getUser();
  }

  public boolean grantCurrentUserRoleAndPermission(String role, String objClass, Long objId, String attribute) {
    raDAO.create(
            new RoleAssignment(
                    role,
                    SecurityUtils.getSubject().getPrincipals().oneByType(PrincipalWrapper.class).getUser(),
                    objClass,
                    objId,
                    attribute),
            getSessionCompliantResource()
    );
    Log.debug("Assign new Permission to current User: Role: " + role + " ObjClass: " + objClass + " ObjID: " + objId, "SecurityHelper");

    return true;
  }

  public boolean grantCurrentUserRoleAndPermission(Roles role, String objClass, Long objId, String attribute) {
    return grantCurrentUserRoleAndPermission(role.getIdentifier(), objClass, objId, attribute);
  }

  public boolean grantCurrentUserRoleAndPermission(Roles role, ObjectClasses objClass, Long objId, String attribute) {
    return grantCurrentUserRoleAndPermission(role.getIdentifier(), objClass.getIdentifier(), objId, attribute);
  }

  public boolean doesUserHavePermission(String permission, String objClass, Long objId,
                                        String attribute) {

    String strObjId = objId != null ? String.valueOf(objId) : "*";
    String strAttribute = attribute != null ? attribute : "*";

    boolean perm = SecurityUtils.getSubject()
            .isPermitted(objClass + ":" + permission + ":" + strObjId + ":" + strAttribute);
    if (!perm) {
      Log.info(
              "Subject isn't permitted: " + permission + "permission" + " objClass " + objClass + " strAttribute " + strAttribute
                      + " strObjId " + strObjId, getClass().getName());
    }
    return perm;
  }

  public boolean doesUserHavePermission(String permission, ObjectClasses objClass, Long objId, String attribute) {
    return doesUserHavePermission(permission, objClass.getIdentifier(), objId, attribute);
  }

  public boolean doesUserHavePermission(String permission, ObjectClasses objClass, Long objId) {
    return doesUserHavePermission(permission, objClass.getIdentifier(), objId, null);
  }

  public boolean doesUserHavePermission(String permission, String objClass, Long objId) {
    return doesUserHavePermission(permission, objClass, objId, null);
  }

  public boolean doesUserHavePermission(String permission, ObjectClasses objClass) {
    return doesUserHavePermission(permission, objClass, null);
  }

  public boolean doesUserHavePermissionType(String permission, ObjectClasses objClass, Long objId) {
    return doesUserHavePermissionType(permission, objClass.getIdentifier(), objId);
  }

  public boolean doesUserHavePermissionType(String permission, String objClass, Long objId) {
    if (ObjectClasses.NONE.getIdentifier().equals(objClass)) {
      return true;
    } else {
      return doesUserHavePermission(objClass + "_" + permission, objClass, objId, null);
    }
  }

  public boolean doesUserHaveRole(long userId, String role, String objClass, Long objId) {
    List<RoleAssignment> ras;
    if (objId != null) {
      ras = raDAO.getRoleAssignments(userId, objClass, objId)
              .stream()
              .filter(ra -> ra.getRole().equals(role))
              .collect(Collectors.toList());
    } else {
      ras = raDAO.getRoleAssignments(userId, objClass)
              .stream()
              .filter(ra -> ra.getRole().equals(role))
              .collect(Collectors.toList());
    }
    return ras.size() > 0;
  }

  public boolean doesUserHaveRole(long userId, Roles role, ObjectClasses objClass, Long objId) {
    return doesUserHaveRole(userId, role.getIdentifier(), objClass.getIdentifier(), objId);
  }

  public boolean doesUserHaveRole(Roles role, ObjectClasses objClass, Long objId) {
    return doesUserHaveRole(SecurityUtils.getSubject().getPrincipals().oneByType(PrincipalWrapper.class).getUser().getId(), role, objClass, objId);
  }

  public boolean doesUserHaveRole(Roles role, ObjectClasses objClass) {
    return doesUserHaveRole(role, objClass, null);
  }

  public String getSessionCompliantResource() {
    Subject subject = SecurityUtils.getSubject();
    return ((PrincipalWrapper) subject.getPrincipal()).getResource();

  }

  public RolePermissionManager getRolePermissionManager() {
    return rolePermissionManager;
  }

  public Set<Long> getPermittedIds(DAOLib daoLib, ObjectClasses objectClass, String... permissions) {
    return getPermittedIds(daoLib, objectClass.getIdentifier(), permissions);
  }

  public Set<Long> getPermittedIds(DAOLib daoLib, String objectClass, String... permissions) {
    return daoLib.getRoleAssignmentDAO().getRoleAssignments(getCurrentUser().getId(), objectClass).stream()
            .filter(r -> {
              Optional<Role> role = getRolePermissionManager().getRole(r.getRole());
              if (!role.isPresent()) {
                return false;
              }
              for (String p : permissions) {
                if (role.get().getPermissions().contains(p)) {
                  return true;
                }
              }
              return false;
            })
            // set -1 when all ids are allowed
            .map(r -> r.getObjId().orElse(-1L)).collect(Collectors.toSet());
  }

  public UserActivationManager getUserActivationManager() {
    return userActivationManager;
  }
}
