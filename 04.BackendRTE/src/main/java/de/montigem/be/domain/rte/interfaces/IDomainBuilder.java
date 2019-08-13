/* (c) https://github.com/MontiCore/monticore */
package de.montigem.be.domain.rte.interfaces;

import javax.xml.bind.ValidationException;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since TODO: add version number
 */
public interface IDomainBuilder<D extends IObject> {
  
  D build() throws ValidationException;
  
}
