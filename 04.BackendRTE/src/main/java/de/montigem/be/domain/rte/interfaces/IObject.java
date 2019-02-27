/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package de.montigem.be.domain.rte.interfaces;

public interface IObject {
  
  long getId();
  
  /**
   * Sets the properties of this object to the values in <i>newValues</i>.
   *
   * @param newValues
   * @return
   */
  void merge(IObject newValues);

  void mergeWithoutAssociations(IObject newValues);
}
