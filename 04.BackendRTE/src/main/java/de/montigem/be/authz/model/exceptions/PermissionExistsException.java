package de.montigem.be.authz.model.exceptions;

public class PermissionExistsException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = -4741035189492917049L;

  public PermissionExistsException(String name) {
    super("Permission " + name + " exists already!");
  }
}
