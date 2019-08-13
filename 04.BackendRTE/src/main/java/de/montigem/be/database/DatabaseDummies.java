/* (c) https://github.com/MontiCore/monticore */
package de.montigem.be.database;

import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.util.DAOLib;
import de.se_rwth.commons.logging.Log;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class DatabaseDummies {
  @Inject
  private DAOLib daoLib;

  @Inject
  private SecurityHelper securityHelper;

  public boolean createDatabaseDummies() {
    Log.info("Creating DummyValues....", getClass().getName());

    // ...

    Log.info("DummyValues created.", getClass().getName());

    return true;
  }
}
