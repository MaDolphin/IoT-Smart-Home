/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */
package de.montigem.be.domain.rte.interfaces;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public interface IDomainOperatorClass<D extends IObject> {
  
  Class<D> getDomainClass();
}
