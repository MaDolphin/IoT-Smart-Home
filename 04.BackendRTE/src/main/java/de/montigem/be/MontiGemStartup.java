/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be;

import de.montigem.be.auth.UserActivationManager;
import de.montigem.be.authz.util.RolePermissionManager;
import de.montigem.be.config.ConfigInitializer;
import de.montigem.be.database.DatabaseDataSource;
import de.montigem.be.domain.cdmodelhwc.classes.domainuser.DomainUser;
import de.montigem.be.util.DAOLib;
import de.montigem.be.util.SensorHandler;
import de.se_rwth.commons.logging.Log;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
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

    new Thread(() -> {
      ZonedDateTime currentTime = ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS);
      try {
        while (true) {
          try {
            SensorHandler.run(daoLib, currentTime);
          }
          catch (IllegalStateException e) {
            Log.warn("0xDEAB: Exception on SensorHandler.run", e);
            System.out.println("illegalstate: " + e);
          }
          currentTime = currentTime.plusSeconds(SensorHandler.FREQUENCY_IN_SECONDS);
          long timeUntilNextRun = ZonedDateTime.now().until(currentTime, ChronoUnit.MILLIS);
          if (timeUntilNextRun > 0) {
            Thread.sleep(timeUntilNextRun);
          }
        }
      }
      catch (InterruptedException e) {
        Log.error("0xDEAD: Thread can't sleep", e);
      }
    }).start();
  }

}
