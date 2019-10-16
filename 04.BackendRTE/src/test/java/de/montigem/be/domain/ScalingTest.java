/* (c) https://github.com/MontiCore/monticore */


package de.montigem.be.domain;

import de.montigem.be.AbstractDomainTest;
import de.montigem.be.marshalling.JsonMarshal;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.shiro.authc.Account;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.core.Response.Status;
import javax.xml.bind.ValidationException;
import java.io.IOException;

public class ScalingTest extends AbstractDomainTest {

  private static final int accountCount = 100000;

  /**
   * time taken to create 100000 accounts: 1056.955s time taken to insert further account: 0.014s
   *
   * @throws IOException
   */
  @Ignore
  @Test
  public void testScaling() throws Exception {
    long start = System.currentTimeMillis();
    for (int i = 0; i < accountCount; i++) {
      testPost("api/domain/accounts", Status.OK,
          Pair.of("account", JsonMarshal.getInstance().marshal(createAccount("account" + i))),
          Account.class, true);
    }
    long end = System.currentTimeMillis();
    Log.trace("time taken to create " + accountCount + " accounts: "
        + (double) (end - start) / 1000 + "s", getClass().getName());

    testPost("api/domain/accounts", Status.OK,
        Pair.of("account",
            JsonMarshal.getInstance().marshal(createAccount("account" + accountCount))),
        Account.class, true);
    Log.trace("time taken to insert further account: "
        + (double) (System.currentTimeMillis() - end) / 1000 + "s", getClass().getName());
  }

  private Account createAccount(String name) throws ValidationException {
   /* AccountBuilder builder = new AccountBuilder();
    builder.name(name);
    builder.startDate(Optional.of(ZonedDateTime.now()));
    builder.startDate(Optional.of(ZonedDateTime.now().plusMonths(4)));
    builder.pspElement(Optional.of("163238761549743"));
    builder.accountType(AccountKindInitializer.DFG);
    Account acc = builder.build();
    assertNotNull(acc);
    return acc;*/
   return null;
  }

}
