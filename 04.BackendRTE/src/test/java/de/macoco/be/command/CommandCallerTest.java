package de.montigem.be.command;

import main.java.be.dtos.rte.DTO;
import de.montigem.be.system.common.dtos.StringContainerDTO;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CommandCallerTest extends DummyValuesforAggregates {
  @Before
  public void init() {
    try {
      loginBootstrapUserWithDBReset();
    }
    catch (Exception e) {
      fail("Couldn´t login bootstrapuser: " + e.getMessage());
    }
  }

  @Test
  public void Info_version_test() {
    DTO result = new Info_version().doRun(securityHelper, daoLib);

    assertTrue(result instanceof StringContainerDTO);
  }
}
