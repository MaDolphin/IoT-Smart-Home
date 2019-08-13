/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be.util;

import de.montigem.be.domain.rte.dao.DynamicEnumDAO;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class DAOLib  extends DAOLibTOP {

  @Inject
  private DynamicEnumDAO dynamicEnumDAO;

  public DynamicEnumDAO getDynamicEnumDAO() {
    return dynamicEnumDAO;
  }
}
