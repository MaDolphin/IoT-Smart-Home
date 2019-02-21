package de.montigem.be.authz.util;

public class RolePermissionNameEncoder {

  public static String getEncodedRoleName(Class<?> objClass, long objId, String roleName) {
    return objClass.getName() + ":" + objId + ":" + roleName;
  }
  
  public static String getEncodedPermissionName(Class<?> objClass, long objId, String permissionName) {
    return objClass.getName() + ":" + objId + ":" + permissionName;
  }
}
