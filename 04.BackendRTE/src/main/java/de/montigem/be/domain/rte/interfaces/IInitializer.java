/* (c) https://github.com/MontiCore/monticore */
package de.montigem.be.domain.rte.interfaces;

import de.montigem.be.domain.rte.dao.AbstractDomainDAO;

/**
 * TODO: Write me!
 *
 *          $Date$
 */
public interface IInitializer<T extends IObject> {

  void addInitialObjects(AbstractDomainDAO<T> dao, String resource);
  
}
