package de.montigem.be.command;

import de.montigem.be.AbstractDomainTest;
import de.montigem.be.domain.cdmodelhwc.classes.macocouser.MacocoUser;
import main.java.be.auth.jwt.MacocoSecurityUtils;
import main.java.be.authz.util.SecurityHelper;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.codec.Base64;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.Optional;

public class SetNewPasswordCommandTest extends AbstractDomainTest {
  @Inject
  private SecurityHelper securityHelper;
  @Inject
  private DAOLib daoLib;

  @Before
  public void init(){
    DatabaseDummies.createUserle(daoLib,securityHelper);
  }
  @Test
  public void test()throws Exception {
    Optional<MacocoUser> user=daoLib.getMacocoUserDAO().find("userle",securityHelper.getSessionCompliantResource());
    SecurityUtils.getSubject().logout();
    if(!user.isPresent()){
      Assert.fail();
    }
    securityManagerInit.init(user.get().getUsername());
    login("userle","userle", Response.Status.OK);

    SetNewPassword sets = new SetNewPassword("userle", "password");
    sets.doRun(securityHelper,daoLib);
    MacocoUser user1= securityHelper.getCurrentUser();
    String oldPwd = MacocoSecurityUtils.encodePassword("password", Base64.decode(user1.getPasswordSaltBase64()));
    Assert.assertTrue(oldPwd.equals(user1.getEncodedPassword().orElse("")));
  }
}
