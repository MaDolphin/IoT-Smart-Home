/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montigem.be.domain.rte.interfaces;

import de.montigem.be.domain.rte.dao.AbstractDomainDAO;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since TODO: add version number
 */
public interface IInitializer<T extends IObject> {

  void addInitialObjects(AbstractDomainDAO<T> dao, String resource);
  
}
