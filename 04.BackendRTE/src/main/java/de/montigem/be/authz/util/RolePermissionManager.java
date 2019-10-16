/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be.authz.util;

import de.montigem.be.authz.Roles;
import de.montigem.be.authz.model.Role;
import de.se_rwth.commons.logging.Log;

import javax.ejb.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Manages roles and permissions in-memory. Provides a lookup mechanism to register and retrieve roles by name.
 */
@Singleton
public class RolePermissionManager {

  private Map<String, Role> roles = new HashMap<>();

  public void createRole(Role role) {
    Log.info("Create role: " + role.getName(), getClass().getName());
    this.roles.put(role.getName(), role);
  }

  public Optional<Role> getRole(String name) {
    Log.debug("Retrieve role for username: " + name, getClass().getName());
    return Optional.ofNullable(roles.get(name));
  }

  public Optional<Role> getRole(Roles roles) {
    return getRole(roles.getIdentifier());
  }
}
