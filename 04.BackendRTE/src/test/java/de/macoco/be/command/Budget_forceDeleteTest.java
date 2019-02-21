package de.montigem.be.command;

import de.montigem.be.AbstractDomainTest;
import main.java.be.authz.util.SecurityHelper;
import main.java.be.command.rte.general.CommandManager;
import de.montigem.be.domain.cdmodelhwc.classes.budget.Budget;
import de.montigem.be.domain.cdmodelhwc.classes.konto.Konto;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

import static org.junit.Assert.fail;

public class Budget_forceDeleteTest extends AbstractDomainTest {

  @Inject
  protected CommandManager commandManager;

  @Inject
  private DatabaseReset databaseReset;

  @Inject
  private DatabaseDummies databaseDummies;

  @Inject
  private SecurityHelper securityHelper;

  @Inject
  public DAOLib daoLib;




  @Before
  public void init() {
    try {
      databaseReset.removeDatabaseEntries(false);
      databaseDummies.createDatabaseDummies();
    } catch (Exception e) {
      fail("Couldn´t login bootstrapuser: " + e.getMessage());
    }

  }

  @Test
  public void forceDeleteBudget(){
    Konto konto=daoLib.getKontoDAO().getAll(securityHelper.getSessionCompliantResource()).get(0);
    konto = daoLib.getKontoDAO().findAndLoad(konto.getId(), daoLib, securityHelper.getSessionCompliantResource()).get();

    Budget gesamt=konto.getGesamtBudget();
    Budget budget=gesamt.getUnterBudgets().get(0);
    System.out.println(budget.getId());
    int pre=daoLib.getBudgetDAO().getAll(securityHelper.getSessionCompliantResource()).size();
    Budget_forceDelete command=new Budget_forceDelete(budget.getId());
    command.doRun(securityHelper,daoLib);
    int post=daoLib.getBudgetDAO().getAll(securityHelper.getSessionCompliantResource()).size();
    Assert.assertEquals(pre - 5, post);
  }


}
