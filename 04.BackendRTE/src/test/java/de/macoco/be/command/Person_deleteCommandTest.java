package de.montigem.be.command;

import de.montigem.be.AbstractDomainTest;
import main.java.be.authz.util.SecurityHelper;
import de.montigem.be.domain.cdmodelhwc.classes.person.Person;
import de.montigem.be.domain.commands.Person_delete;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;

public class Person_deleteCommandTest extends AbstractDomainTest {
    @Inject
    private DAOLib daoLib;
    @Inject
    private SecurityHelper securityHelper;
    @Inject
    private DatabaseReset databaseReset;

    @Before
    public void init(){
        DatabaseDummies.createUserle(daoLib,securityHelper);
        databaseReset.removeDatabaseEntries(true);
    }

    @Test
    public void doRunTest() throws Exception {
        Person person = CreateDummyPerson.createPersonOhneVertrag();
        person=daoLib.getPersonDAO().create(person,securityHelper.getSessionCompliantResource());
        Person_delete p = new Person_delete(person.getId());
        p.doRun(securityHelper,daoLib);
        Assert.assertEquals(0,daoLib.getPersonDAO().getAll(securityHelper.getSessionCompliantResource()).size());
    }
}
