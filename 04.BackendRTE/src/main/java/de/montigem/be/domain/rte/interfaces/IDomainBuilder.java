/* (c) https://github.com/MontiCore/monticore */
package de.montigem.be.domain.rte.interfaces;

import javax.xml.bind.ValidationException;

/**
 * TODO: Write me!
 *
 *          $Date$
 */
public interface IDomainBuilder<D extends IObject> {
  
  D build() throws ValidationException;
  
}
