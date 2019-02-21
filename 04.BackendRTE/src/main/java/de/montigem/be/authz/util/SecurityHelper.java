package de.montigem.be.authz.util;

import de.montigem.be.auth.jwt.PrincipalWrapper;
import de.montigem.be.authz.model.Role;
import de.montigem.be.util.DAOLib;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import javax.ejb.Stateless;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Stateless
public class SecurityHelper {

  public String getSessionCompliantResource() {
    Subject subject = SecurityUtils.getSubject();
    return ((PrincipalWrapper) subject.getPrincipal()).getResource();
  }

  public Set<Long> getPermittedIds(DAOLib daoLib, String objectClass, String... permissions) {
   /* return daoLib.getRoleAssignmentDAO().getRoleAssignments(getCurrentUser().getId(), objectClass).stream()
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
            .map(r -> r.getObjId() == null ? -1L : r.getObjId()).collect(Collectors.toSet());*/
   return new HashSet<>();
  }
}