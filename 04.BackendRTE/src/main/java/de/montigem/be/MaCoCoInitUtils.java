/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montigem.be;

import de.montigem.be.authz.util.RolePermissionManager;
import de.montigem.be.domain.cdmodelhwc.classes.types.DynamicEnum;
import de.montigem.be.domain.rte.dao.DynamicEnumDAO;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class MaCoCoInitUtils {

  private static boolean propertiesDerived = false;

  private static boolean isDebugMode = true;

  private static boolean isOnServer = false;

  private static String serverURL = "http://localhost:4200";

  public static void initRoles(RolePermissionManager rpm) {
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
        InputStream stream = MaCoCoInitUtils.class.getClassLoader()
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
        stream = MaCoCoInitUtils.class.getClassLoader()
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
        Log.error(MaCoCoInitUtils.class.getName()
            + " Critical error: Could not read server property files!");
      }
    }
    System.out
        .println("---------------------------Properties derived------------------------------");
    return isDebugMode;
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
