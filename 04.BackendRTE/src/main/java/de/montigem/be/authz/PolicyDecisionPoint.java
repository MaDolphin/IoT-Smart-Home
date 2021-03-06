/* (c) https://github.com/MontiCore/monticore */

package de.montigem.be.authz;

import de.montigem.be.auth.jwt.PrincipalWrapper;
import de.montigem.be.authz.model.Role;
import de.montigem.be.authz.util.RolePermissionManager;
import de.montigem.be.domain.cdmodelhwc.classes.roleassignment.RoleAssignment;
import de.montigem.be.domain.cdmodelhwc.daos.RoleAssignmentDAO;
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
import java.util.Collection;
import java.util.Optional;
import java.util.Properties;

public class PolicyDecisionPoint extends AuthorizingRealm {

  private RoleAssignmentDAO dao;
  private RolePermissionManager rpm;

  public PolicyDecisionPoint() throws NamingException {
    Properties props = new Properties();
    props.setProperty(Context.INITIAL_CONTEXT_FACTORY,
            "org.apache.openejb.client.LocalInitialContextFactory");
    InitialContext context = new InitialContext(props);
    dao = (RoleAssignmentDAO) context.lookup("java:global/montigem-be/RoleAssignmentDAO");
    rpm = (RolePermissionManager) context.lookup("java:global/montigem-be/RolePermissionManager");
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
    Collection<RoleAssignment> assignments = dao.getRoleAssignments(user.getId());

    /**
     * Transform role assignments into shiro's string based format
     * objClass:permission:objId:attribute
     */
    for (RoleAssignment assignment : assignments) {
      Optional<Role> role = rpm.getRole(assignment.getRole());
      if (role.isPresent()) {
        for (String perm : role.get().getPermissions()) {
          String inst = assignment.getObjId().isPresent()?
              String.valueOf(assignment.getObjId().get()) : "*";
          String attr = assignment.getAttribute().orElse("*");
          res.addStringPermission(assignment.getObjClass() + ":" + perm + ":" + inst + ":" + attr);
        }
      } else {
        Log.error(getClass().getName() + " MAB0x0026: role " + assignment.getRole() + " is not present!");
      }
    }

    return res;
  }

  @Override
  protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
          throws AuthenticationException {
    throw new AuthenticationException();
  }

}
