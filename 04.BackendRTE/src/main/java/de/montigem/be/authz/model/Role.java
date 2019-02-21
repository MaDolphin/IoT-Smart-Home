package de.montigem.be.authz.model;

import de.montigem.be.authz.Roles;

import java.util.List;

public class Role {
  private String name;
  private List<String> permissions;

  public Role() {
  }

  public Role(String name, List<String> permissions) {
    this.name = name;
    this.permissions = permissions;
  }

  public Role(Roles roles, List<String> permissions) {
    this.name = roles.getIdentifier();
    this.permissions = permissions;
  }

  public String getName() {
    return name;
  }

  public void setName(String value) {
    this.name = value;
  }

  public List<String> getPermissions() {
    return permissions;
  }
}
