/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */
package de.montigem.be;

import de.montigem.be.auth.UserActivationManager;
import de.montigem.be.authz.ObjectClasses;
import de.montigem.be.authz.Permissions;
import de.montigem.be.authz.Roles;
import de.montigem.be.authz.model.Role;
import de.montigem.be.authz.util.RolePermissionManager;
import de.montigem.be.config.Config;
import de.montigem.be.database.DatabaseDataSource;
import de.montigem.be.database.DatabaseDataSourceUtil;
import de.montigem.be.domain.cdmodelhwc.classes.domainuser.DomainUser;
import de.montigem.be.domain.cdmodelhwc.classes.domainuseractivationstatus.DomainUserActivationStatus;
import de.montigem.be.domain.cdmodelhwc.classes.roleassignment.RoleAssignment;
import de.montigem.be.domain.cdmodelhwc.classes.types.DynamicEnum;
import de.montigem.be.domain.cdmodelhwc.daos.DomainUserDAO;
import de.montigem.be.domain.cdmodelhwc.daos.RoleAssignmentDAO;
import de.montigem.be.domain.rte.dao.DynamicEnumDAO;
import de.se_rwth.commons.logging.Log;

import javax.mail.MessagingException;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.*;

public class MontiGemInitUtils {

  private static boolean propertiesDerived = false;

  private static boolean isDebugMode = true;

  private static boolean isOnServer = false;

  private static String serverURL = "http://localhost:4200";

  public static void initRoles(RolePermissionManager rpm) {
    Role roleLeser = new Role(Roles.LESER, Arrays.asList(
            Permissions.READ
    ));

    // populate roles with permissions
    Role roleAdmin = new Role(Roles.ADMIN, Arrays.asList(

            Permissions.USER_CREATE,
            Permissions.USER_READ,
            Permissions.USER_UPDATE,
            Permissions.USER_DELETE,

            Permissions.ROLE_CREATE,
            Permissions.ROLE_READ,
            Permissions.ROLE_UPDATE,
            Permissions.ROLE_DELETE,

            Permissions.SETTINGS_CREATE,
            Permissions.SETTINGS_READ,
            Permissions.SETTINGS_UPDATE,
            Permissions.SETTINGS_DELETE,

            Permissions.CREATE,
            Permissions.UPDATE,
            Permissions.READ,
            Permissions.DELETE
    ));

    rpm.createRole(roleAdmin);
    rpm.createRole(roleLeser);
  }

  public static void initDynamicEnums(DynamicEnumDAO dynumDAO, String resource, Class<?> clazz) {
    Log.debug("initialize predefined data values", clazz.getName());
    List<DynamicEnum> dynamicEnumList = dynumDAO.getAll(resource);
    if (dynamicEnumList != null && dynamicEnumList.isEmpty()) {
      CreateDynamicEnums.initializeDynEnums(dynumDAO, resource);
    }
  }

  public static boolean isDebug() {
    if (!propertiesDerived) {
      try {
        Properties serverProps = new Properties();
        InputStream stream = MontiGemInitUtils.class.getClassLoader()
                .getResourceAsStream("devserver.properties");
        if (stream != null) {
          System.out.println(
                  "---------------------------Devserver.properties------------------------------");
          serverProps.load(stream);
          stream.close();
          serverURL = serverProps.getProperty("devurl");
          isOnServer = Boolean.parseBoolean(serverProps.getProperty("isOnServer"));
          isDebugMode = true;
          propertiesDerived = true;
          return isDebugMode;
        }
        stream = MontiGemInitUtils.class.getClassLoader()
                .getResourceAsStream("testserver.properties");
        if (stream != null) {
          System.out.println(
                  "---------------------------testserver.properties------------------------------");
          serverProps.load(stream);
          stream.close();
          serverURL = serverProps.getProperty("testurl");
          isOnServer = Boolean.parseBoolean(serverProps.getProperty("isOnServer"));
          isDebugMode = false;
          propertiesDerived = true;
          return isDebugMode;
        }
        System.out.println(
                "---------------------------No Properties found------------------------------");
        propertiesDerived = true;
        isOnServer = false;
        isDebugMode = true;
        return isDebugMode;
      } catch (IOException e) {
        Log.error(MontiGemInitUtils.class.getName()
                + " Critical error: Could not read server property files!");
      }
    }
    System.out
            .println("---------------------------Properties derived------------------------------");
    return isDebugMode;
  }

  public static void initDebugUser(DomainUserDAO mudao, RoleAssignmentDAO raDAO, String resource,
                                   boolean isDebug, Class<?> clazz) {
    DomainUser userAccountReader, userAccountCreator, userProject, userPlan;

    // Reader
    String pwdAccountReader = isDebug ? "reader" : null;
    userAccountReader = mudao
            .create(new DomainUser(DomainUserActivationStatus.AKTIVIERT, true, "reader",
                    "macoco2017@outlook.de", Optional.of(pwdAccountReader), ZonedDateTime.now(), Optional.of("MAR")), resource);
    if (null == userAccountReader) {
      Log.error(clazz.getName()
              + " MAB0x0011: Critical error: Could not create bootstrap user reader!");
      return;
    }
    Log.debug("bootstrap user reader created", clazz.getName());

    // Creator
    String pwdAccountCreator = isDebug ? "creator" : null;
    userAccountCreator = mudao
            .create(new DomainUser(DomainUserActivationStatus.AKTIVIERT, true, "creator",
                    "macoco2018@outlook.de", Optional.of(pwdAccountCreator), ZonedDateTime.now(), Optional.of("MAC")), resource);
    if (null == userAccountCreator) {
      Log.error(clazz.getName()
              + " MAB0x0012: Critical error: Could not create bootstrap user creator!");
      return;
    }
    Log.debug("bootstrap user creator created", clazz.getName());

    // Project
    String pwdProject = isDebug ? "project" : null;
    userProject = mudao.create(new DomainUser(DomainUserActivationStatus.AKTIVIERT, true, "project",
            "macoco2019@outlook.de", Optional.of(pwdProject), ZonedDateTime.now(), Optional.of("MP")), resource);
    if (null == userProject) {
      Log.error(clazz.getName()
              + " MAB0x0013: Critical error: Could not create bootstrap user project!");
      return;
    }
    Log.debug("bootstrap user project created", clazz.getName());

    // Plan
    String pwdPlan = isDebug ? "plan" : null;
    userPlan = mudao.create(new DomainUser(DomainUserActivationStatus.AKTIVIERT, true, "plan",
            "macoco2020@outlook.de", Optional.of(pwdPlan), ZonedDateTime.now(), Optional.of("MPL")), resource);
    if (null == userPlan) {
      Log.error(clazz.getName()
              + " MAB0x0015: Critical error: Could not create bootstrap user plan!");
      return;
    }
    Log.debug("bootstrap user plan created", clazz.getName());

    // assign function account reader to bootstrap user
    raDAO.create(
            new RoleAssignment(Roles.LESER, userAccountReader, ObjectClasses.USER,
                    null, null), resource);
    raDAO.create(
            new RoleAssignment(Roles.LESER, userAccountReader, ObjectClasses.ROLE_ASSIGNMENT,
                    null, null), resource);

    // assign function project to bootstrap user

  }

  public static void initAdmin(DomainUserDAO mudao, RoleAssignmentDAO raDAO,
                               UserActivationManager activationManager, Role roleAdmin, String adminUsername,
                               String adminEmail, String adminInitials, String resource,
                               Class<?> clazz) {
    if (!mudao.find(adminUsername, resource).isPresent()) {
      String pwd = "passwort";
      DomainUser user = mudao
              .create(
                      new DomainUser(DomainUserActivationStatus.AKTIVIERT/* TODO auf isDebug setzen*/, true,
                              adminUsername,
                              adminEmail, Optional.of(pwd), ZonedDateTime.now(), Optional.ofNullable(adminInitials)), resource);
      if (null == user) {
        System.err.println("!!!!!!!!!!!!!!!!! Critical error: Could not create bootstrap user! ");
        Log.error(clazz.getName() + " Critical error: Could not create bootstrap user!");
        return;
      }
      if (Config.SEND_MAILS) {
        try {
          String datenbankBezeichner = DatabaseDataSourceUtil.getDatenbankBezeichner(resource);
          activationManager.sendActivationEmail(user.getEmail(), user.getUsername(), resource,
                  datenbankBezeichner);
        } catch (MessagingException e) {
          Log.debug(e.getMessage(), "MontiGemInitUtils");
        }
      }

      // assign function admin to bootstrap user if they don't exist in the database yet
      raDAO.create(
              new RoleAssignment(roleAdmin.getName(), user, ObjectClasses.USER, null, null),
              resource);
      raDAO.create(
              new RoleAssignment(roleAdmin.getName(), user, ObjectClasses.ROLE_ASSIGNMENT, null,
                      null), resource);

      Log.debug("Admin user created", clazz.getName());
    }
  }

  public static void initAdminFromInstanceDB(DomainUserDAO mudao, RoleAssignmentDAO raDAO,
                                             RolePermissionManager rpm, DatabaseDataSource databaseDataSource,
                                             UserActivationManager userActivationManager, String resource, boolean isDebug,
                                             Class<?> clazz) {

    Map<String, String> admin = databaseDataSource.getUserForDB(resource);
    if (!admin.isEmpty()) {
      String adminUsername = admin.keySet().iterator().next();
      String adminEmail = admin.get(adminUsername);

      Optional<Role> roleAdmin = rpm.getRole(Roles.ADMIN);
      if (roleAdmin.isPresent()) {
        MontiGemInitUtils
                .initAdmin(mudao, raDAO, userActivationManager, roleAdmin.get(), adminUsername,
                        adminEmail, null, resource, clazz);
      }

      // Additional Admins for Development
      if (isDebug) {
        for (int i = 2; i < 6; i++) {
          MontiGemInitUtils
                  .initAdmin(mudao, raDAO, userActivationManager, roleAdmin.get(), adminUsername + i,
                          adminEmail + i, null, resource, clazz);
        }
      }
    }
  }

  public static boolean isOnServer() {
    if (!propertiesDerived) {
      isDebug();
    }
    return isOnServer;
  }

  public static String getServerURL() {
    Map<String, String> env = System.getenv();
    if (env.containsKey("SERVER_DOMAIN")) {
      return env.get("SERVER_DOMAIN");
    }
    return serverURL;
  }
}
