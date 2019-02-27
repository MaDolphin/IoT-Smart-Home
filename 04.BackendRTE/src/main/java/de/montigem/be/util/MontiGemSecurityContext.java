/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package de.montigem.be.util;

public class MontiGemSecurityContext {
  private boolean isAuthenticated;

  public MontiGemSecurityContext(boolean isAuthenticated) {
    this.isAuthenticated = isAuthenticated;
  }

  public boolean isAuthenticated() {
    return isAuthenticated;
  }

  public void setAuthenticated(boolean authenticated) {
    isAuthenticated = authenticated;
  }
}
