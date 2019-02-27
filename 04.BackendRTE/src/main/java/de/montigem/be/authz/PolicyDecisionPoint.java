/*
 *  (c) Monticore license: https://github.com/MontiCore/monticore
 */

package de.montigem.be.authz;

import de.montigem.be.auth.jwt.PrincipalWrapper;
import de.montigem.be.authz.util.RolePermissionManager;
import de.montigem.be.domain.rte.interfaces.IObject;
import de.se_rwth.commons.logging.Log;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

//TODO GV, SVa
public class PolicyDecisionPoint extends AuthorizingRealm {

//  private RoleAssignmentDAO dao;
  private RolePermissionManager rpm;

  public PolicyDecisionPoint() throws NamingException {
    Properties props = new Properties();
    props.setProperty(Context.INITIAL_CONTEXT_FACTORY,
            "org.apache.openejb.client.LocalInitialContextFactory");
    InitialContext context = new InitialContext(props);
 //   dao = (RoleAssignmentDAO) context.lookup("java:global/macoco-be/RoleAssignmentDAO");
    rpm = (RolePermissionManager) context.lookup("java:global/macoco-be/RolePermissionManager");
  }

  /**
   * @return Always false, as this realm does not support authentication, only authorization
   */
  @Override
  public boolean supports(AuthenticationToken token) {
    return false;
  }

  @Override
  protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
    Log.debug("retrieving auth info", getClass().getName());
    SimpleAccount res = new SimpleAccount(principals, "");

    IObject user = ((PrincipalWrapper)principals.getPrimaryPrincipal()).getUser();
 //   Collection<RoleAssignment> assignments = dao.getRoleAssignments(user.getId());

//    Log.info("---------- RAs ----------", getClass().getName());
//    if (assignments != null) {
//      for (RoleAssignment ra : assignments) {
//        Log.info(ra.getObjClass() + ": " + ra.getRole() + " (" + ra.getObjId() + ")", getClass().getName());
//      }
//    } else {
//      Log.info("no RAs!", getClass().getName());
//    }

    /**
     * Transform role assignments into shiro's string based format
     * objClass:permission:objId:attribute
     */
   /* for (RoleAssignment assignment : assignments) {
      Optional<Role> role = rpm.getRole(assignment.getRole());
      if (role.isPresent()) {
        for (String perm : role.get().getPermissions()) {
          String inst = (assignment.getObjId() != null) ?
              String.valueOf(assignment.getObjId()) : "*";
          String attr = (assignment.getAttribute() != null) ? assignment.getAttribute() : "*";
          res.addStringPermission(assignment.getObjClass() + ":" + perm + ":" + inst + ":" + attr);
        }
      } else {
        Log.error(getClass().getName() + " MAB0x0026: role " + assignment.getRole() + " is not present!");
      }
    }*/
//    Log.info("---------- Permissions ----------", getClass().getName());
//    if(res.getStringPermissions() != null) {
//      for(String perm : res.getStringPermissions()) {
//        Log.info(perm, getClass().getName());
//      }
//    } else {
//      Log.info("no permissions!", getClass().getName());
//    }

    return null;
  }

  @Override
  protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
          throws AuthenticationException {
    throw new AuthenticationException();
  }

}
