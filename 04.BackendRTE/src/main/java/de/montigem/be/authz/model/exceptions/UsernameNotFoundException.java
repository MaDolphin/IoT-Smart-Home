package de.montigem.be.authz.model.exceptions;

public class UsernameNotFoundException extends Exception {

  /**
   * 
   */
  private static final long serialVersionUID = -5054188795103484372L;

  public UsernameNotFoundException() {
    super("Username not found!");
  }
}
