/* (c) https://github.com/MontiCore/monticore */
package de.montigem.be.domain.cdmodelhwc.classes.roleassignment;

public class RoleAssignmentProxy extends RoleAssignmentProxyTOP {

  private String userName;

  public RoleAssignmentProxy(RoleAssignment roleAssignment) {
    super(roleAssignment);
    this.userName = roleAssignment.getUser().getUsername();
  }

  public String getUserName() {
    return this.userName;
  }

}
