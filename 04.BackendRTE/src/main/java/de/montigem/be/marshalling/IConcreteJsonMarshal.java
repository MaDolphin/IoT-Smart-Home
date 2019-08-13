/* (c) https://github.com/MontiCore/monticore */
package de.montigem.be.marshalling;

import de.montigem.be.error.JsonException;

import java.lang.reflect.Type;
import java.util.Optional;

/**
 * Interface for different implementations to marshal to or unmarshal from JSON.
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public interface IConcreteJsonMarshal {
  
  /**
   * Implementation marshals object o to a JSON String representation
   *
   * @param o Object to be marshalled
   * @return JSON
   */
  String marshal(Object o);

  /**
   * An implementation unmarshals a JSON String s to an object of type clazz. If the operation was
   * unsuccessful, it returns {@link <code>null</code>}
   *
   * @param s
   * @param type
   * @return
   */
  <T> T unmarshal(String s, Type type) throws JsonException;

  /**
   * An implementation unmarshals a JSON String s to an object of type clazz. If the operation was
   * unsuccessful, it returns {@link <code>null</code>}
   *
   * @param s
   * @param type
   * @return
   */
  <T> T unmarshal(Object s, Type type) throws JsonException;
  
  /**
   * An implementation unmarshals a JSON String s to an object of type clazz. If the operation was
   * unsuccessful, it returns {@link <code>null</code>}
   *
   * @param s
   * @param clazz
   * @return
   */
  <T> T unmarshal(String s, Class<T> clazz) throws JsonException;

  /**
   * An implementation unmarshals a JSON String s to an object of type clazz. If the operation was
   * unsuccessful, it returns {@link <code>null</code>}
   *
   * @param s
   * @param clazz
   * @return
   */
  <T> T unmarshal(Object s, Class<T> clazz) throws JsonException;

  /**
   * An implementation unmarshals a JSON String s to an object of type clazz. If the operation was
   * unsuccessful, it returns {@link Optional#empty()}
   *
   * @param s
   * @param type
   * @return
   */
  <T> Optional<T> unmarshalOptional(String s, Type type);

  /**
   * An implementation unmarshals a JSON String s to an object of type clazz. If the operation was
   * unsuccessful, it returns {@link Optional#empty()}
   *
   * @param s
   * @param type
   * @return
   */
  <T> Optional<T> unmarshalOptional(Object s, Type type);

  /**
   * An implementation unmarshals a JSON String s to an object of type clazz. If the operation was
   * unsuccessful, it returns {@link Optional#empty()}
   *
   * @param s
   * @param clazz
   * @return
   */
  <T> Optional<T> unmarshalOptional(String s, Class<T> clazz);

  /**
   * An implementation unmarshals a JSON String s to an object of type clazz. If the operation was
   * unsuccessful, it returns {@link Optional#empty()}
   *
   * @param s
   * @param clazz
   * @return
   */
  <T> Optional<T> unmarshalOptional(Object s, Class<T> clazz);
}
