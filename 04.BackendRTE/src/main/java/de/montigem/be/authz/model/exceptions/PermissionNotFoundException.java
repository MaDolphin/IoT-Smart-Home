package de.montigem.be.authz.model.exceptions;

public class PermissionNotFoundException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = -2620380661125463173L;
  
  public PermissionNotFoundException(String name) {
    super("Permission " + name + " not found!");
  }
}
