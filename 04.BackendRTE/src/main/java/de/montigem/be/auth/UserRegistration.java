/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package de.montigem.be.auth;

public class UserRegistration {

  private String username;
  private String password;
  private String email;
  private String initials;

  public UserRegistration(String username, String password, String email, String initials) {
    this.username = username;
    this.password = password;
    this.email = email;
    this.initials = initials;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getEmail() {
    return email;
  }

  public String getInitials() {
    return initials;
  }

  public boolean isValid() {
    return getUsername() != null && getPassword() != null && getEmail() != null && getInitials() != null
            && !"".equals(getUsername().trim()) && !"".equals(getPassword().trim())
            && !"".equals(getEmail().trim());
  }
}
