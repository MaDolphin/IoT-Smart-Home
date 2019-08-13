/* (c) https://github.com/MontiCore/monticore */
package de.montigem.be.auth.jwt;

import de.montigem.be.authz.Permissions;
import de.montigem.be.authz.model.Role;
import de.montigem.be.authz.util.RolePermissionManager;
import de.montigem.be.domain.cdmodelhwc.classes.roleassignment.RoleAssignment;

import java.util.BitSet;
import java.util.List;
import java.util.Optional;

public enum PermissionFlags {
  USER(Permissions.USER_READ),
  NBITS(null);

  private String permission;

  PermissionFlags(String permission) {
    this.permission = permission;
  }

  public static BitSet asBitset(List<RoleAssignment> roleAssignments, RolePermissionManager rolePermissionManager) {
    BitSet bs = new BitSet(NBITS.ordinal());

    roleAssignments.forEach(r -> {
      Optional<Role> role = rolePermissionManager.getRole(r.getRole());
      if (!role.isPresent()) {
        return;
      }
      for (PermissionFlags p : values()) {
        if (role.get().getPermissions().contains(p.permission)) {
          bs.set(p.ordinal(), true);
        }
      }
    });

    return bs;
  }

  public static String asBinaryString(List<RoleAssignment> roleAssignments, RolePermissionManager rolePermissionManager) {
    StringBuilder sb = new StringBuilder(NBITS.ordinal());

    BitSet bs = asBitset(roleAssignments, rolePermissionManager);

    for (int i = 0; i < NBITS.ordinal(); i++) {
      sb.append(bs.get(i) ? '1' : '0');
    }

    return sb.toString();
  }
}
