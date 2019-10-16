/* (c) https://github.com/MontiCore/monticore */
package de.montigem.be.database;

import de.montigem.be.CreateDynamicEnums;
import de.montigem.be.authz.util.SecurityHelper;
import de.montigem.be.util.DAOLib;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class DatabaseReset {

  @Inject
  private DAOLib daoLib;

  @Inject
  private SecurityHelper securityHelper;

  public void removeDatabaseEntries(boolean deleteUser) {
    if (deleteUser) {
      removeUserEntries();
    }

    CreateDynamicEnums.initializeDynEnums(daoLib.getDynamicEnumDAO(), securityHelper.getSessionCompliantResource());
  }

  public void removeUserEntries() {
    // User
    daoLib.getRoleAssignmentDAO().removeAllExceptFromAdmin();
    daoLib.getDomainUserDAO().removeAllExceptAdmin(securityHelper.getSessionCompliantResource());
  }
}
