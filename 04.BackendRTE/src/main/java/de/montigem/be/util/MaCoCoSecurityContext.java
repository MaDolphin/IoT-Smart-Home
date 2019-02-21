package de.montigem.be.util;

public class MaCoCoSecurityContext {
  private boolean isAuthenticated;

  public MaCoCoSecurityContext(boolean isAuthenticated) {
    this.isAuthenticated = isAuthenticated;
  }

  public boolean isAuthenticated() {
    return isAuthenticated;
  }

  public void setAuthenticated(boolean authenticated) {
    isAuthenticated = authenticated;
  }
}
