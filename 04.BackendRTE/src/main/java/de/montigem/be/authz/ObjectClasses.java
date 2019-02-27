/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package de.montigem.be.authz;

import java.util.Arrays;
import java.util.Optional;

public enum ObjectClasses {

  NONE ("none","Nichts");

  String identifier;
  String name;

  ObjectClasses(String identifier, String name) {
    this.identifier = identifier;
    this.name = name;
  }

  public java.lang.String getIdentifier() {
    return identifier;
  }

  public java.lang.String getName() {
    return name;
  }

  public static Optional<ObjectClasses> objectClasses(String identifier) {
    return Arrays.stream(ObjectClasses.values()).filter(v ->
            v.getIdentifier().equals(identifier)).findFirst();
  }

  @Override
  public String toString() {
    return getIdentifier();
  }
}
