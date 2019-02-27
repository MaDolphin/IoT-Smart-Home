/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package de.montigem.be.marshalling;

import java.util.HashSet;
import java.util.Set;

public class TypeAdapterResolver<T> {

  private Set<Class<? extends T>> subtypes;

  TypeAdapterResolver() {
    subtypes = new HashSet<>();
  }

  public TypeAdapterResolver<T> registerSubtype(Class<? extends T> subtype) {
    subtypes.add(subtype);
    return this;
  }

  protected RuntimeTypeAdapterFactory<T> getFactory(Class<T> type) {
    RuntimeTypeAdapterFactory<T> typeFactory = RuntimeTypeAdapterFactory
        .of(type, "typeName", true);

    subtypes.forEach(typeFactory::registerSubtype);

    return typeFactory;
  }

  public RuntimeTypeAdapterFactory<T> getFactory() {
    return null;
  }
}
