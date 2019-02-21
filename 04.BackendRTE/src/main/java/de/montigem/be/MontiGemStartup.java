package de.montigem.be;

import de.montigem.be.authz.util.RolePermissionManager;
import de.montigem.be.config.ConfigInitializer;
import de.montigem.be.database.DatabaseDataSource;
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
  public MontiGemStartup(DAOLib daoLib, RolePermissionManager rpm, DatabaseDataSource databaseDataSource) {
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
    }
  }

  /*@Inject
  public MontiGemStartup(DAOLib daoLib, RolePermissionManager rpm, DatabaseDataSource databaseDataSource, UserActivationManager activationManager) {
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

      List<MacocoUser> macocoUserList = daoLib.getMacocoUserDAO().getAll(databaseName);
      if (isDebug && macocoUserList != null && macocoUserList.isEmpty()) {
        MontiGemInitUtils.initDebugUser(daoLib.getMacocoUserDAO(), daoLib.getRoleAssignmentDAO(), databaseName, isDebug, getClass());
      }

      MontiGemInitUtils
          .initAdminFromInstanceDB(daoLib.getMacocoUserDAO(), daoLib.getRoleAssignmentDAO(), rpm, databaseDataSource, activationManager, databaseName, isDebug,
              getClass());
    }
  }*/
}
