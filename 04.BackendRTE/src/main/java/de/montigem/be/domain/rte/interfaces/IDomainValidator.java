/* (c) https://github.com/MontiCore/monticore */
package de.montigem.be.domain.rte.interfaces;

import java.util.Optional;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public interface IDomainValidator<D extends IObject> {

  // Returns a list of validation errors oder leer list if valid
  Optional<String> getValidationErrors(D d);

  // Checks if the given object valid
  default boolean isValid(D d) {
    return !getValidationErrors(d).isPresent();
  }

}
