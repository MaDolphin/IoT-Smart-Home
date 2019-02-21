package de.montigem.be.command;

import de.montigem.be.AbstractDomainTest;
import main.java.be.authz.util.SecurityHelper;
import de.montigem.be.domain.cdmodelhwc.classes.budget.Budget;
import de.montigem.be.domain.cdmodelhwc.classes.konto.Konto;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

public class Budget_umbuchenTest extends AbstractDomainTest {
    @Inject
    private DatabaseReset databaseReset;

    @Inject
    private DatabaseDummies databaseDummies;

    @Inject
    private DAOLib daoLib;

    @Inject
    private SecurityHelper securityHelper;

    @Before
    public void init(){
        databaseReset.removeDatabaseEntries(false);
    }
    @Test
    public void doRunTest() throws Exception{
        Konto konto1=CreateDummyKonto.createKonto1();
        Konto konto2=CreateDummyKonto.createKonto2();
        konto1=daoLib.getKontoDAO().createAndLoad(konto1,daoLib,securityHelper.getSessionCompliantResource());
        konto2=daoLib.getKontoDAO().createAndLoad(konto2,daoLib,securityHelper.getSessionCompliantResource());
        Budget bud1=konto1.getGesamtBudget().getUnterBudgets().get(0).getUnterBudgets().get(0);
        Budget bud2=konto2.getGesamtBudget().getUnterBudgets().get(0).getUnterBudgets().get(0);
        int erg=bud1.getBuchungen().size()+bud2.getBuchungen().size();
        Budget_umbuchen b =new Budget_umbuchen(bud1.getId(),bud2.getId());
        b.doRun(securityHelper,daoLib);
        bud2=daoLib.getBudgetDAO().findAndLoad(bud2.getId(),daoLib,securityHelper.getSessionCompliantResource()).get();
        bud1=daoLib.getBudgetDAO().findAndLoad(bud1.getId(),daoLib,securityHelper.getSessionCompliantResource()).get();
        Assert.assertEquals(erg,bud2.getBuchungen().size());
        Assert.assertEquals(0,bud1.getBuchungen().size());
    }
}
