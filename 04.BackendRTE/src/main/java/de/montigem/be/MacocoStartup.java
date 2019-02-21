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
public class MacocoStartup {

  private boolean isDebug = MaCoCoInitUtils.isDebug();

  private final String resource = "TestDB";

  @Inject
  public MacocoStartup(DAOLib daoLib, RolePermissionManager rpm, DatabaseDataSource databaseDataSource) {
    // set global time zone
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    databaseDataSource.initalDataSource(MaCoCoInitUtils.isOnServer());
    // disable failquick for logger
    Log.enableFailQuick(false);

    MaCoCoInitUtils.initRoles(rpm);

    // register bootstrap user
    Log.debug("initialize constants", getClass().getName());
    ConfigInitializer.initializeConstants();

    List<String> databaseNames = databaseDataSource.getAllDatabaseNames();

    for (String databaseName : databaseNames) {
      // initialize dynamic enums
      MaCoCoInitUtils.initDynamicEnums(daoLib.getDynamicEnumDAO(), databaseName, getClass());
    }
  }

  /*@Inject
  public MacocoStartup(DAOLib daoLib, RolePermissionManager rpm, DatabaseDataSource databaseDataSource, UserActivationManager activationManager) {
    // set global time zone
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    databaseDataSource.initalDataSource(MaCoCoInitUtils.isOnServer());
    // disable failquick for logger
    Log.enableFailQuick(false);

    MaCoCoInitUtils.initRoles(rpm);

    // register bootstrap user
    Log.debug("initialize constants", getClass().getName());
    ConfigInitializer.initializeConstants();

    List<String> databaseNames = databaseDataSource.getAllDatabaseNames();

    for (String databaseName : databaseNames) {
      // initialize dynamic enums
      MaCoCoInitUtils.initDynamicEnums(daoLib.getDynamicEnumDAO(), databaseName, getClass());

      List<MacocoUser> macocoUserList = daoLib.getMacocoUserDAO().getAll(databaseName);
      if (isDebug && macocoUserList != null && macocoUserList.isEmpty()) {
        MaCoCoInitUtils.initDebugUser(daoLib.getMacocoUserDAO(), daoLib.getRoleAssignmentDAO(), databaseName, isDebug, getClass());
      }

      MaCoCoInitUtils
          .initAdminFromInstanceDB(daoLib.getMacocoUserDAO(), daoLib.getRoleAssignmentDAO(), rpm, databaseDataSource, activationManager, databaseName, isDebug,
              getClass());
    }
  }*/
}
