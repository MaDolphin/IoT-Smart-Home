package de.montigem.be.auth;

import de.montigem.be.AbstractDomainTest;
import de.montigem.be.domain.cdmodelhwc.classes.macocouser.MacocoUser;
import main.java.be.authz.ObjectClasses;
import main.java.be.authz.Permissions;
import main.java.be.authz.Roles;
import main.java.be.authz.util.SecurityHelper;
import de.montigem.be.domain.cdmodelhwc.classes.konto.Konto;
import de.montigem.be.domain.cdmodelhwc.classes.person.Person;
import de.montigem.be.domain.cdmodelhwc.classes.roleassignment.RoleAssignment;
import de.montigem.be.domain.cdmodelhwc.classes.vertrag.Vertrag;
import de.montigem.be.util.TestUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;

public class SecurityHelperTest extends AbstractDomainTest {

    @Inject
    private SecurityHelper securityHelper;

    @Inject
    private DAOLib daoLib;

    @Inject
    private DatabaseReset databaseReset;

    @Inject
    private DatabaseDummies databaseDummies;



    @Before
    public void init(){
        try {
            databaseReset.removeDatabaseEntries(true);
        } catch (Exception e) {
            fail("Couldn´t login bootstrapuser: " + e.getMessage());
        }
    }

    @Test
    public void getPermittedIdsKontoAnlegerTest() throws Exception {
        MacocoUser user = TestUtil.setupKontoAnleger(daoLib, securityHelper);
        securityManagerInit.init(user.getUsername()); // Login test user
        long onlyPermittedId = TestUtil.createKonto(daoLib, securityHelper);

        Set<Long> ids = securityHelper.getPermittedIds(daoLib, ObjectClasses.ACCOUNT, Permissions.ACCOUNT_READ);

        assertEquals("Only one Konto should be permitted!", 1, ids.size());
        assertEquals("Only the created Konto should be returned!", onlyPermittedId, ids.toArray()[0]);
    }

    @Test
    public void doesUserHavePermssionForAccountTest()throws Exception{
        MacocoUser user= TestUtil.createUser(daoLib, securityHelper);
        long kontoid=daoLib.getKontoDAO().create(CreateDummyKonto.createKonto1(),securityHelper.getSessionCompliantResource()).getId();
      RoleAssignment role= new RoleAssignment(Roles.LESER, user, ObjectClasses.ACCOUNT, kontoid, null);
      daoLib.getRoleAssignmentDAO().create(role, securityHelper.getSessionCompliantResource());
      //logout(Response.Status.OK);
      assertTrue(securityHelper.doesUserHavePermssionForAccount(Permissions.ACCOUNT_READ,user.getId(),kontoid));
    }
    @Test
    public void doesUserHavePermssionForAccountTest2()throws Exception{
        MacocoUser user= TestUtil.createUser(daoLib, securityHelper);
        long kontoid=daoLib.getKontoDAO().create(CreateDummyKonto.createKonto1(),securityHelper.getSessionCompliantResource()).getId();
        //logout(Response.Status.OK);
        assertFalse(securityHelper.doesUserHavePermssionForAccount(Permissions.ACCOUNT_READ,user.getId(),kontoid));
    }
    @Test
    public void doesUserHavePermssionForAccountTest3()throws Exception{
        MacocoUser user= TestUtil.createUser(daoLib, securityHelper);
        long kontoid=daoLib.getKontoDAO().create(CreateDummyKonto.createKonto1(),securityHelper.getSessionCompliantResource()).getId();
        RoleAssignment role= new RoleAssignment(Roles.LESER, user, ObjectClasses.ACCOUNT, null, null);
        daoLib.getRoleAssignmentDAO().create(role, securityHelper.getSessionCompliantResource());
        //logout(Response.Status.OK);
        assertTrue(securityHelper.doesUserHavePermssionForAccount(Permissions.ACCOUNT_READ,user.getId(),kontoid));
    }

    //TODO MV: fix compilation errors
   /* @Test
    public void grantCurrentUserRoleAndPermissionTest() throws Exception{
        MacocoUser user= TestUtil.createUser(daoLib, securityHelper);
        //SecurityUtils.getSubject().logout();

        long kontoid=daoLib.getKontoDAO().create(CreateDummyKonto.createKonto1(),securityHelper.getSessionCompliantResource()).getId();
        long kontoid1=daoLib.getKontoDAO().create(CreateDummyKonto.createKonto2(),securityHelper.getSessionCompliantResource()).getId();
        securityManagerInit.init(user.getUsername());
        login("test","test",Response.Status.OK);
        System.out.println(securityHelper.getCurrentUser().getUsername());
        securityHelper.grantCurrentUserRoleAndPermission(Roles.LESER, ObjectClasses.ACCOUNT,kontoid, null);
        assertTrue(securityHelper.doesUserHavePermssionForAccount(Permissions.ACCOUNT_READ,user.getId(),kontoid));
        assertFalse(securityHelper.doesUserHavePermssionForAccount(Permissions.ACCOUNT_READ,user.getId(),kontoid1));
    }*/
    @Test
    public void hasPersonalPermissionTest()throws Exception{
        MacocoUser user= TestUtil.createUser(daoLib, securityHelper);
        securityManagerInit.init(user.getUsername());
        RoleAssignment role= new RoleAssignment(Roles.PERSONAL, user, ObjectClasses.PERSONAL, null, null);
        daoLib.getRoleAssignmentDAO().create(role, securityHelper.getSessionCompliantResource());
        assertTrue(securityHelper.hasPersonalPermission());
    }

    @Test
    public void hasPersonalPermissionTest2()throws Exception{
        MacocoUser user= TestUtil.createUser(daoLib, securityHelper);
        securityManagerInit.init(user.getUsername());
        assertFalse(securityHelper.hasPersonalPermission());
    }

    @Test
    public void hasFakultaetPermissionTest()throws Exception{
        MacocoUser user= TestUtil.createUser(daoLib, securityHelper);
        securityManagerInit.init(user.getUsername());
        RoleAssignment role= new RoleAssignment(Roles.FAKULTAET_MANAGER, user, ObjectClasses.ACCOUNT, null, null);
        daoLib.getRoleAssignmentDAO().create(role, securityHelper.getSessionCompliantResource());
        assertTrue(securityHelper.hasFakultaetPermission());
    }

    @Test
    public void hasFakultaetPermissionTest2()throws Exception{
        MacocoUser user= TestUtil.createUser(daoLib, securityHelper);
        securityManagerInit.init(user.getUsername());
        assertFalse(securityHelper.hasFakultaetPermission());
    }

    @Test
    public void hasReadPermissionVertragTest()throws Exception{
        MacocoUser user= TestUtil.createUser(daoLib, securityHelper);
        Konto konto1=daoLib.getKontoDAO().create(CreateDummyKonto.createKonto1(),securityHelper.getSessionCompliantResource());
        Konto konto2=daoLib.getKontoDAO().create(CreateDummyKonto.createKonto2(),securityHelper.getSessionCompliantResource());
        Person person=daoLib.getPersonDAO().create(CreateDummyPerson.createPerson1(Optional.of(konto1), Optional.of(konto2)),
                securityHelper.getSessionCompliantResource());
        Vertrag vertrag=person.getAnstellungsartens().get(0).getVertraeges().get(0);
        securityManagerInit.init(user.getUsername());
        Assert.assertFalse(securityHelper.hasReadPermission(vertrag));
    }
    @Test
    public void hasReadPermissionVertragTest2()throws Exception{
        MacocoUser user= TestUtil.createUser(daoLib, securityHelper);
        Konto konto1=daoLib.getKontoDAO().create(CreateDummyKonto.createKonto1(),securityHelper.getSessionCompliantResource());
        Konto konto2=daoLib.getKontoDAO().create(CreateDummyKonto.createKonto2(),securityHelper.getSessionCompliantResource());
        Person person=daoLib.getPersonDAO().create(CreateDummyPerson.createPerson1(Optional.of(konto1), Optional.of(konto2)),
                securityHelper.getSessionCompliantResource());
        Vertrag vertrag=person.getAnstellungsartens().get(0).getVertraeges().get(0);
        securityManagerInit.init(user.getUsername());
        RoleAssignment role= new RoleAssignment(Roles.PERSONAL, user, ObjectClasses.PERSONAL, null, null);
        daoLib.getRoleAssignmentDAO().create(role, securityHelper.getSessionCompliantResource());
        Assert.assertTrue(securityHelper.hasReadPermission(vertrag));
    }


    @Test
    public void hasReadPermissionPersonTest()throws Exception{
        MacocoUser user= TestUtil.createUser(daoLib, securityHelper);
        Konto konto1=daoLib.getKontoDAO().create(CreateDummyKonto.createKonto1(),securityHelper.getSessionCompliantResource());
        Konto konto2=daoLib.getKontoDAO().create(CreateDummyKonto.createKonto2(),securityHelper.getSessionCompliantResource());
        Person person=daoLib.getPersonDAO().create(CreateDummyPerson.createPerson1(Optional.of(konto1), Optional.of(konto2)),
                securityHelper.getSessionCompliantResource());
        securityManagerInit.init(user.getUsername());
        RoleAssignment role= new RoleAssignment(Roles.PERSONAL, user, ObjectClasses.PERSONAL, null, null);
        daoLib.getRoleAssignmentDAO().create(role, securityHelper.getSessionCompliantResource());
        Assert.assertTrue(securityHelper.hasReadPermission(person));
    }
    @Test
    public void hasReadPermissionPersonTest2()throws Exception{
        MacocoUser user= TestUtil.createUser(daoLib, securityHelper);
        Konto konto1=daoLib.getKontoDAO().create(CreateDummyKonto.createKonto1(),securityHelper.getSessionCompliantResource());
        Konto konto2=daoLib.getKontoDAO().create(CreateDummyKonto.createKonto2(),securityHelper.getSessionCompliantResource());
        Person person=daoLib.getPersonDAO().create(CreateDummyPerson.createPerson1(Optional.of(konto1), Optional.of(konto2)),
                securityHelper.getSessionCompliantResource());
      securityManagerInit.init(user.getUsername());
        Assert.assertFalse(securityHelper.hasReadPermission(person));
    }
    @Test
    public void hasKontoReadOrFakultaetPermissionTest() throws Exception{
        MacocoUser user= TestUtil.createUser(daoLib, securityHelper);
        Konto konto1=daoLib.getKontoDAO().create(CreateDummyKonto.createKonto1(),securityHelper.getSessionCompliantResource());
        securityManagerInit.init(user.getUsername());
        Assert.assertFalse(securityHelper.hasKontoReadOrFakultaetPermission(konto1));
    }

    @Test
    public void hasKontoReadOrFakultaetPermissionTest2() throws Exception{
        MacocoUser user= TestUtil.createUser(daoLib, securityHelper);
        Konto konto1=daoLib.getKontoDAO().create(CreateDummyF1Konto.createKonto1(),securityHelper.getSessionCompliantResource());
        securityManagerInit.init(user.getUsername());
        RoleAssignment role= new RoleAssignment(Roles.FAKULTAET_MANAGER, user, ObjectClasses.ACCOUNT, null, null);
        daoLib.getRoleAssignmentDAO().create(role, securityHelper.getSessionCompliantResource());
        Assert.assertTrue(securityHelper.hasKontoReadOrFakultaetPermission(konto1));
    }
    @Test
    public void hasKontoReadOrFakultaetPermissionTest3() throws Exception{
        MacocoUser user= TestUtil.createUser(daoLib, securityHelper);
        Konto konto1=daoLib.getKontoDAO().create(CreateDummyKonto.createKonto1(),securityHelper.getSessionCompliantResource());
        securityManagerInit.init(user.getUsername());
        RoleAssignment role= new RoleAssignment(Roles.ACCOUNT_ADMIN, user, ObjectClasses.ACCOUNT, null, null);
        daoLib.getRoleAssignmentDAO().create(role, securityHelper.getSessionCompliantResource());
        Assert.assertTrue(securityHelper.hasKontoReadOrFakultaetPermission(konto1));
    }
    @Test
    public void hasKontoReadOrFakultaetPermissionTest4() throws Exception{
        MacocoUser user= TestUtil.createUser(daoLib, securityHelper);
        Konto konto1=daoLib.getKontoDAO().create(CreateDummyKonto.createKonto1(),securityHelper.getSessionCompliantResource());
        securityManagerInit.init(user.getUsername());
        RoleAssignment role= new RoleAssignment(Roles.ACCOUNT_ADMIN, user, ObjectClasses.ACCOUNT, konto1.getId(), null);
        daoLib.getRoleAssignmentDAO().create(role, securityHelper.getSessionCompliantResource());
        Assert.assertTrue(securityHelper.hasKontoReadOrFakultaetPermission(konto1));
    }

    @Test
    public void getPermittedIdsTest() throws Exception {
        MacocoUser user= TestUtil.createUser(daoLib, securityHelper);
        long kontoid=daoLib.getKontoDAO().create(CreateDummyKonto.createKonto1(),securityHelper.getSessionCompliantResource()).getId();
        securityManagerInit.init(user.getUsername());
        RoleAssignment role= new RoleAssignment(Roles.LESER, user, ObjectClasses.ACCOUNT, kontoid, null);
        daoLib.getRoleAssignmentDAO().create(role, securityHelper.getSessionCompliantResource());
        //logout(Response.Status.OK);
        assertEquals(1,securityHelper.getPermittedIds(daoLib,ObjectClasses.ACCOUNT,Permissions.ACCOUNT_READ).size());
    }

}
