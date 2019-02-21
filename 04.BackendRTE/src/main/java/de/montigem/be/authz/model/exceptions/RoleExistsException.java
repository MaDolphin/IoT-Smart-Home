package de.montigem.be.authz.model.exceptions;

public class RoleExistsException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = 2649053587725922360L;

  public RoleExistsException(String name) {
    super("Role " + name + " exists already!");
  }
}
