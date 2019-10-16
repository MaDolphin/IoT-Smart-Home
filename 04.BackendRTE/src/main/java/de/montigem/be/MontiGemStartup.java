/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be;

import de.montigem.be.auth.UserActivationManager;
import de.montigem.be.authz.util.RolePermissionManager;
import de.montigem.be.config.ConfigInitializer;
import de.montigem.be.database.DatabaseDataSource;
import de.montigem.be.domain.cdmodelhwc.classes.domainuser.DomainUser;
import de.montigem.be.util.DAOLib;
import de.se_rwth.commons.logging.Log;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.util.List;
import java.util.TimeZone;

@Startup
@Singleton
public class MontiGemStartup {

  private boolean isDebug = MontiGemInitUtils.isDebug();

  private final String resource = "TestDB";

  @Inject
  public MontiGemStartup(DAOLib daoLib, RolePermissionManager rpm, DatabaseDataSource databaseDataSource, UserActivationManager activationManager) {
    // set global time zone
    // set global time zone
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    databaseDataSource.initalDataSource(MontiGemInitUtils.isOnServer());
    // disable failquick for logger
    Log.enableFailQuick(false);

    MontiGemInitUtils.initRoles(rpm);

    // register bootstrap user
    Log.debug("initialize constants", getClass().getName());
    ConfigInitializer.initializeConstants();

    List<String> databaseNames = databaseDataSource.getAllDatabaseNames();

    for (String databaseName : databaseNames) {
      // initialize dynamic enums
      MontiGemInitUtils.initDynamicEnums(daoLib.getDynamicEnumDAO(), databaseName, getClass());
      List<DomainUser> DomainUserList = daoLib.getDomainUserDAO().getAll(databaseName);
      if (isDebug && DomainUserList != null && DomainUserList.isEmpty()) {
        MontiGemInitUtils.initDebugUser(daoLib.getDomainUserDAO(), daoLib.getRoleAssignmentDAO(), databaseName, isDebug, getClass());
      }

      MontiGemInitUtils
              .initAdminFromInstanceDB(daoLib.getDomainUserDAO(), daoLib.getRoleAssignmentDAO(), rpm, databaseDataSource, activationManager, databaseName, isDebug,
                      getClass());
    }
  }

}
