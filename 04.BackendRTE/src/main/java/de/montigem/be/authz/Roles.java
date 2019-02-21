package de.montigem.be.authz;

import java.util.Arrays;
import java.util.Optional;

public enum Roles {
  ADMIN("admin","Administrator"),
  LESER("leser", "Leser"),
  ACCOUNT_MANAGER("accountManager", "Kontomanager"),
  ACCOUNT_ANLEGER("accountAnleger", "Kontoanleger"),
  PLAN_ACCOUNT_ANLEGER("planAccountAnleger", "Plankontoanleger"),
  FAKULTAET_MANAGER("facultaetManager", "Fakultätmanager"),
  PERSONAL("personal", "Personal"),
  ACCOUNT_ADMIN("accountAdmin", "Finanzadministrator"),
  ACCOUNT_EDITOR("accountEditor", "Kontobearbeiter");

  String identifier;
  String name;

  Roles(String identifier, String name) {
    this.identifier = identifier;
    this.name = name;
  }

  public java.lang.String getIdentifier() {
    return identifier;
  }

  public java.lang.String getName() {
    return name;
  }

  public static Optional<Roles> roles(String identifier) {
    return Arrays.stream(Roles.values()).filter(v ->
            v.getIdentifier().equals(identifier)).findFirst();
  }

  @Override
  public String toString() {
    return getIdentifier();
  }
}
